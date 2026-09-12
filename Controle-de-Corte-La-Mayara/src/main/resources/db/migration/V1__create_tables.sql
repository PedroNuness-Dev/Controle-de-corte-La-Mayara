CREATE TABLE enfestador (
                            id_enfestador INTEGER AUTO_INCREMENT PRIMARY KEY,
                            nome VARCHAR(100),
                            ativo boolean default true,
                            data_de_cadastro DATE DEFAULT (CURRENT_DATE)
);

CREATE TABLE cortador (
                       id_cortador INTEGER AUTO_INCREMENT PRIMARY KEY,
                       nome VARCHAR(100),
                       ativo boolean default true,
                       data_de_cadastro DATE DEFAULT (CURRENT_DATE)
);

CREATE TABLE lote(
                      id_lote INTEGER AUTO_INCREMENT PRIMARY KEY,
                      numero_lote INTEGER NOT NULL,
                      ano_lote INTEGER NOT NULL
);
CREATE TABLE corte (
                       id_corte    INTEGER          NOT NULL AUTO_INCREMENT,
                       data_de_corte   DATE,
                       data_de_registro   DATE,
                       nome_modelo     VARCHAR(255),
                       quantidade_total INT,
                       corte_status VARCHAR(50),
                       lote_formatado varchar(50),
                       lote        INTEGER NOT NULL ,
                       id_enfestador  INTEGER ,
                       id_cortador    INTEGER  ,
                       observacao TEXT,

                       CONSTRAINT pk_corte PRIMARY KEY (id_corte),
                       CONSTRAINT fk_corte_lote       FOREIGN KEY (lote)       REFERENCES lote(id_lote),
                       CONSTRAINT fk_corte_enfestador FOREIGN KEY (id_enfestador) REFERENCES enfestador(id_enfestador),
                       CONSTRAINT fk_corte_cortador   FOREIGN KEY (id_cortador)   REFERENCES cortador(id_cortador)
);
