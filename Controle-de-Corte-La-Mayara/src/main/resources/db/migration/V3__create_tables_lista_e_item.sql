CREATE TABLE lista(
                      id INTEGER PRIMARY KEY AUTO_INCREMENT,
                      titulo VARCHAR(100) NOT NULL,
                      descricao VARCHAR(250),
                      dataDeCriacao DATE NOT NULL DEFAULT current_date
);


CREATE TABLE item(
                     id INTEGER PRIMARY KEY AUTO_INCREMENT,
                     nome VARCHAR(100) NOT NULL,
                     observacao VARCHAR(250),
                     atencao BOOLEAN NOT NULL DEFAULT FALSE,
                     posicao INTEGER NOT NULL,
                     lista INTEGER,

                     CONSTRAINT fk_item_lista FOREIGN KEY (lista) REFERENCES lista(id)
);