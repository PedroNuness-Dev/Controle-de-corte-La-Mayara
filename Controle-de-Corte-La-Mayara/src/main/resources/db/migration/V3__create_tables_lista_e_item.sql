CREATE TABLE lista(
                      id INTEGER PRIMARY KEY AUTO_INCREMENT,
                      titulo VARCHAR(100) NOT NULL,
                      descricao VARCHAR(250),
                      data_de_criacao DATE NOT NULL DEFAULT current_date
);


CREATE TABLE item(
                     id INTEGER PRIMARY KEY AUTO_INCREMENT,
                     nome VARCHAR(100) NOT NULL,
                     quantidade INTEGER NOT NULL,
                     observacao VARCHAR(250),
                     atencao BOOLEAN NOT NULL DEFAULT FALSE,
                     posicao INTEGER NOT NULL,
                     lista_id INTEGER,
                     data_de_insercao DATE NOT NULL DEFAULT current_date,

                     CONSTRAINT fk_item_lista FOREIGN KEY (lista_id) REFERENCES lista(id)
);