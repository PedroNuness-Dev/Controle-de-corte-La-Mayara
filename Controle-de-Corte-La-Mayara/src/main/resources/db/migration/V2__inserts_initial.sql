INSERT INTO cortador(nome)
VALUES ('Ermeson'),('Irã'),('Rodrigo'),('Paulinho'),('Rodolfo');

INSERT INTO enfestador(nome)
VALUES ('Irã'),('Paulinho'),('Rodolfo'),('Rodrigo'),('Ermeson') ;

INSERT INTO lote(numero_lote,ano_lote)
VALUES  (015,'2026');

INSERT INTO corte(data_de_corte, data_de_registro , nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date, 'Patrick', 800, 'PENDENTE', '001/26', 1, null, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date, 'Aurora', 800, 'PENDENTE', '002/26', 1, null, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date, 'Melissa', 800, 'PENDENTE', '003/26', 1, null, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date, 'Santiago', 800, 'PENDENTE', '004/26', 1, null, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date, 'Helena', 800, 'PENDENTE', '005/26', 1, null, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date, 'Benjamin', 800, 'PENDENTE', '006/26', 1, null, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date, 'Valentina', 800, 'PENDENTE', '007/26', 1, null, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date,'Theo', 800, 'ENFESTADO', '008/26', 1, 1, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date, 'Cecília', 800, 'ENFESTADO', '009/26', 1, 1, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date,'Gael', 800, 'ENFESTADO', '010/26', 1, 1, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date, 'Isabella', 800, 'ENFESTADO', '011/26', 1, 1, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (null,current_date, 'Arthur', 800, 'ENFESTADO', '012/26', 1, 1, null, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (current_date,current_date, 'Sophia', 800, 'CORTADO', '013/26', 1, 1, 1, '4 x 200');

INSERT INTO corte(data_de_corte,data_de_registro, nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (current_date,current_date, 'Miguel', 800, 'CORTADO', '014/26', 1, 1, 1, '4 x 200');

INSERT INTO corte(data_de_corte, data_de_registro,nome_modelo, quantidade_total, corte_status, lote_formatado, lote, id_enfestador, id_cortador, observacao)
VALUES (current_date,current_date, 'Alice', 800, 'CORTADO', '015/26', 1, 1, 1, '4 x 200');