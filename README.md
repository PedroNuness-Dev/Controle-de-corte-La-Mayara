# Controle de Corte

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_4-6DB33F?style=flat&logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular_21-DD0031?style=flat&logo=angular&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL_8-4479A1?style=flat&logo=mysql&logoColor=white)
![H2](https://img.shields.io/badge/H2-blue?style=flat)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat&logo=flyway&logoColor=white)
![Docker](https://img.shields.io/badge/Docker_Compose-2496ED?style=flat&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=flat&logo=swagger&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=flat&logo=junit5&logoColor=white)
![License](https://img.shields.io/badge/license-proprietário-red?style=flat)

Sistema completo de gestão do processo produtivo de uma confecção têxtil: do registro do corte na mesa até o acompanhamento de quem enfestou, quem cortou e quanto já foi produzido no mês — tudo em um painel único, com backend próprio e deploy containerizado.

> Aplicação full stack em uso real de produção, composta por API REST (Spring Boot), painel web (Angular) e orquestração via Docker Compose, com scripts prontos para subir, atualizar e encerrar o sistema em um servidor local.

> 🔒 **Software proprietário e de uso exclusivo.** Este repositório não é open source. Cópia, redistribuição, sublicenciamento ou uso fora dos termos acordados com o autor não são permitidos. Veja a seção [Licença](#licença) para os termos completos.

## Sumário

- [O problema](#o-problema)
- [Como funciona](#como-funciona)
- [Stack](#stack)
- [Arquitetura](#arquitetura)
- [Estrutura do repositório](#estrutura-do-repositório)
- [Modelo de domínio](#modelo-de-domínio)
- [Rodando o projeto](#rodando-o-projeto)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Endpoints principais](#endpoints-principais)
- [Ciclo de vida de um corte](#ciclo-de-vida-de-um-corte)
- [Tratamento de erros](#tratamento-de-erros)
- [Backup e atualização em produção](#backup-e-atualização-em-produção)
- [Testes](#testes)
- [Licença](#licença)

## O problema

Numa confecção, o corte de tecido passa por várias mãos até virar peça pronta: alguém enfesta o tecido, alguém corta, o lote precisa de numeração sequencial, e no fim do mês é preciso saber quantos cortes foram feitos e por quem. Fazer esse controle em papel ou planilha solta é fácil de perder, difícil de auditar e não escala com o volume de produção.

O **Controle de Corte** resolve isso com um sistema dedicado que:

1. registra cada corte com modelo, quantidade, lote e observações;
2. atribui e transfere a responsabilidade de enfesto e corte entre os colaboradores cadastrados;
3. numera os lotes automaticamente por ano, com opção de ajuste manual;
4. acompanha o status de cada corte (pendente → enfestado → cortado, ou cancelado);
5. gera relatórios de quantidade de cortes por mês;
6. organiza listas de apoio (compras, pendências) com itens marcáveis;
7. permite abrir um chamado de suporte por e-mail direto do painel.

## Como funciona

```
                    ┌──────────────────────────┐
                    │   Painel Angular          │
                    │  (home, cortes, histórico,│
                    │   config, suporte, notas) │
                    └────────────┬──────────────┘
                                 │ HTTP/REST
                                 ▼
                    ┌──────────────────────────┐
                    │   API Spring Boot         │
                    │  Controller → Service     │
                    │  → Repository (JPA)       │
                    └────────────┬──────────────┘
                                 │
                 ┌───────────────┼────────────────┐
                 ▼               ▼                ▼
           MySQL / H2       Flyway (schema     JavaMailSender
          (persistência)    versionado)        (envio de suporte)
```

Um corte nasce vinculado a um **lote** (numeração sequencial do ano) e evolui de status conforme é enfestado e cortado por um **enfestador** e um **cortador**. Consultas de listagem por mês/status usam cache (`@Cacheable`) para reduzir carga no banco, invalidado automaticamente (`@CacheEvict`) a cada criação, atualização ou cancelamento.

## Stack

**Backend**
- **Java 21** + **Spring Boot 4**
- **Spring Data JPA** + Hibernate
- **Flyway** — versionamento do schema (MySQL)
- **MySQL 8** (produção) / **H2** (perfil de testes/dev)
- **Spring Cache** — cache das consultas de corte por mês/status
- **Spring Mail** — envio de e-mails de suporte
- **Bean Validation** (Jakarta)
- **SpringDoc OpenAPI + Swagger UI** — documentação interativa
- **JUnit 5 + Mockito** — testes unitários de serviço
- **Lombok** · **MapStruct** · **Maven**

**Frontend**
- **Angular 21** (standalone components)
- **TypeScript** + **SCSS**
- **Vitest** — testes unitários

**Infraestrutura**
- **Docker** + **Docker Compose** (MySQL + backend + frontend em rede isolada)
- Scripts `.bat` para subir, atualizar (com backup automático do banco) e encerrar o ambiente

## Arquitetura

O backend segue a separação clássica em camadas, com um controller e service por entidade de domínio:

| Camada | Responsável | O que faz |
|---|---|---|
| Cortadores | `CortadorController` / `CortadorService` | cadastro, ativação/desativação e overview dos cortadores |
| Enfestadores | `EnfestadorController` / `EnfestadorService` | cadastro, ativação/desativação e overview dos enfestadores |
| Lotes | `LoteController` / `LoteService` | busca (com criação automática) e incremento/decremento do lote do ano vigente |
| Cortes | `CorteController` / `CorteService` | criação, atualização, busca por mês/status/nome/lote, troca de responsáveis e cancelamento |
| Listas | `ListaController` / `ListaService` | criação de listas com itens (apoio a compras/pendências) |
| Relatórios | `RelatorioController` / `RelatorioService` | quantidade de cortes registrados em um mês/ano |
| E-mail | `MailSenderController` / `MailService` | envio de mensagens de suporte por e-mail (HTML) |

Todas as rotas usam DTOs de request/response dedicados (com `@Valid`) e mapeamento via **MapStruct**, mantendo as entidades JPA isoladas da camada HTTP.

## Estrutura do repositório

Este é um monorepo com três projetos independentes:

```
Controle-de-Corte-La-Mayara/     # API REST (Spring Boot)
├── src/main/java/.../controller/    # endpoints REST
├── src/main/java/.../service/       # regras de negócio (cache, transações)
├── src/main/java/.../repository/    # Spring Data JPA
├── src/main/java/.../model/         # entidades (Corte, Cortador, Enfestador, Lote, Lista, Item)
├── src/main/java/.../dto/           # request/response
├── src/main/java/.../mapper/        # MapStruct
├── src/main/java/.../exception/     # handler global de erros
└── src/main/resources/db/migration/ # scripts Flyway (V1...V4)

controle-de-corte-angular/       # Painel web (Angular standalone)
└── src/app/
    ├── components/                  # home, content, historico, config, suporte, nota, sidebar
    ├── services/                    # clients HTTP por domínio (corte, cortador, enfestador, lote, relatorio, suporte)
    └── interfaces/                  # tipagem dos DTOs consumidos da API

application/                     # Orquestração de deploy
├── docker-compose.yaml              # mysql + backend-spring + frontend-angular
├── config/                          # variáveis de ambiente (backend.env, mysql.env)
├── app-run.bat                      # sobe o ambiente completo
├── atualizar-sistema.bat            # backup do banco + git pull + rebuild
└── stop.bat                         # encerra os containers
```

## Modelo de domínio

| Entidade | Descrição |
|---|---|
| **Cortador** | colaborador responsável por cortar o tecido; pode ser ativado/desativado |
| **Enfestador** | colaborador responsável por enfestar (organizar as camadas de tecido antes do corte) |
| **Lote** | numeração sequencial de produção, controlada por ano (`numero_lote/ano`) |
| **Corte** | registro central: modelo, quantidade, datas de registro/corte, status, lote e responsáveis vinculados |
| **Lista** / **Item** | listas de apoio com itens que podem ser marcados como concluídos e sinalizados com atenção |

## Rodando o projeto

### Com Docker (recomendado)

```bash
git clone <url-do-repositorio>
cd application
# ajuste as variáveis em config/backend.env e config/mysql.env
docker compose up -d
```

- API em `http://localhost:8080` · Swagger em `http://localhost:8080/swagger-ui.html`
- Painel Angular em `http://localhost:4200`

No Windows, os scripts prontos cobrem o dia a dia:

```bat
app-run.bat             :: sobe o ambiente completo
atualizar-sistema.bat   :: faz backup do MySQL, atualiza o código (git pull) e reconstrói os containers
stop.bat                :: encerra todos os containers
```

### Rodando localmente (sem Docker)

**Backend**
```bash
cd Controle-de-Corte-La-Mayara
./mvnw spring-boot:run
```
Por padrão sobe com o profile `test`, usando banco H2 em memória (console habilitado).

**Frontend**
```bash
cd controle-de-corte-angular
npm install
npm start
```

## Variáveis de ambiente

**Backend** (`application/config/backend.env` em produção, ou variáveis de ambiente locais):

```properties
# conexão com o MySQL (perfil prod)
BD_HOST=<host-do-mysql>
BD_PORT=3306
BD_USER=<usuario>
BD_PASSWORD=<senha>
BD_NAME=<nome-do-banco>

# envio de e-mails de suporte
MAIL_USERNAME=<email-remetente>
MAIL_PASSWORD=<senha-de-app-do-email>
```

**MySQL** (`application/config/mysql.env`):

```properties
MYSQL_ROOT_PASSWORD=<senha-root>
MYSQL_DATABASE=<nome-do-banco>
```

| Profile | Banco | Uso |
|---|---|---|
| `test` (padrão) | H2 em memória | Desenvolvimento local |
| `prod` | MySQL 8 | Produção (via Docker Compose) |

## Endpoints principais

| Método | Rota | O que faz |
|---|---|---|
| `GET` | `/corte/{id}` | busca um corte por ID |
| `GET` | `/corte/buscar/mes?mes=&ano=` | lista cortes de um mês/ano (ou do mês atual) |
| `GET` | `/corte/buscar/mes/status?status=` | filtra cortes por status em um mês/ano |
| `GET` | `/corte/buscar?tipo=` | busca cortes por nome do modelo ou número de lote |
| `POST` | `/corte` | registra um novo corte |
| `PUT` | `/corte/atualizar/{id}` | atualiza dados de um corte |
| `PUT` | `/corte/{idCorte}/enfestador/{idEnfestador}` | atribui/troca o enfestador do corte |
| `PUT` | `/corte/{idCorte}/cortador/{idCortador}` | atribui/troca o cortador do corte |
| `PUT` | `/corte/{idCorte}/cancelar` | cancela um corte |
| `DELETE` | `/corte/{id}` | exclui um corte |
| `GET` / `POST` / `PUT` / `PATCH` | `/cortador`, `/enfestador` | CRUD e ativação/desativação de colaboradores |
| `GET` | `/lote/buscar` | busca (ou cria) o lote do ano atual |
| `PUT` | `/lote/incrementar` / `/lote/decrementar` | ajusta o número do lote vigente |
| `POST` | `/lista` | cria uma lista com itens |
| `GET` | `/relatorio/buscar/quantidade/registros?mes=&ano=` | quantidade de cortes registrados no mês |
| `POST` | `/email/send` | envia um e-mail de suporte |

Documentação completa e interativa no Swagger após subir a API.

## Ciclo de vida de um corte

```
PENDENTE ──► ENFESTADO ──► CORTADO
    │
    └──────────────────► CANCELADO
```

- **PENDENTE** — corte registrado, aguardando enfesto
- **ENFESTADO** — já possui enfestador atribuído
- **CORTADO** — já possui cortador atribuído, produção concluída
- **CANCELADO** — corte cancelado a qualquer momento do fluxo

## Tratamento de erros

Toda exceção é capturada centralmente pelo `GlobalExceptionHandler` e traduzida em um formato padronizado (`ErrorResponse` com status, mensagem, path e, quando aplicável, lista de erros de campo):

| Cenário | Status retornado |
|---|---|
| Recurso não encontrado (`ResourceNotFoundException`) | `404 Not Found` |
| Operação inválida (`InvalidOperationException`) | `400 Bad Request` |
| Falha no envio de e-mail (`MailSendException`) | `400 Bad Request` |
| Argumento inválido (ex: status ou mês inexistente) | `400 Bad Request` |
| Erro de validação de campos (`@Valid`) | `400 Bad Request` (com lista de erros por campo) |
| Erro inesperado | `500 Internal Server Error` |

## Backup e atualização em produção

O script `atualizar-sistema.bat` automatiza o ciclo seguro de atualização do sistema:

1. gera um dump do banco MySQL (`mysqldump`) com timestamp em `backups/`;
2. atualiza o código-fonte (`git pull`);
3. reconstrói e reinicia os containers (`docker compose up -d --build`).

## Testes

Os serviços centrais têm cobertura de testes unitários com **JUnit 5 + Mockito** no backend.

```bash
# backend
cd Controle-de-Corte-La-Mayara
./mvnw test
```

## Licença

**Software proprietário. Todos os direitos reservados.**

Este código-fonte é de propriedade exclusiva do autor e é disponibilizado apenas para uso privado e/ou mediante licenciamento comercial acordado diretamente com o autor. Salvo autorização expressa e por escrito:

- é **proibida** a cópia, reprodução, distribuição ou publicação deste código, no todo ou em parte;
- é **proibida** a sublicença, revenda ou cessão a terceiros;
- é **proibido** o uso para fins distintos daqueles definidos no acordo com o cliente/licenciado;
- o(s) titular(es) do repositório retêm todos os direitos autorais e de propriedade intelectual sobre o código, design e documentação.

O uso deste software por parte de terceiros só é válido mediante contrato ou licença específica firmada com o autor, na qual serão definidos o escopo, a exclusividade (quando aplicável) e as condições comerciais.

> Este aviso é um resumo em linguagem simples e não substitui um contrato de licenciamento ou cessão formal. Para uma venda ou transferência de direitos, recomenda-se formalizar os termos por escrito com apoio jurídico.

---

<div align="center">

**Desenvolvido por Pedro Nunes**

</div>