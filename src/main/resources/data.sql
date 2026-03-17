-- Región de Arica y Parinacota (XV)
INSERT INTO communes (name, created_at, updated_at, deleted_at) VALUES
                                                                    ('Punta Arenas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Puerto Natales', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Porvenir', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);


INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAB THOMAS FENTON',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAI ADULTO MAGALLANES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PROGRAMA RESIDENCIAL  MAGALLANES / PAI PR MIXTO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PROGRAMA KSORREM KANENNA /PAI PR MUJERES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAC PARA PERSONAS EN SITUACION DE CALLE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAI ADOLESCENTE PUNTA ARENAS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PROYECTO PAI INFRACTORES CAUDA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAI ADULTO NATALES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAI ADOLESCENTE NATALES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);


INSERT INTO sexs(name,created_at,updated_at,deleted_at) VALUES('MASCULINO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('FEMENINO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('MASCULINO TRANS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('FEMENINO TRANS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('NO BINARIO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('OTRO GENERO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('NINGUN GENERA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('PREFIERO NO DECIRLO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);


INSERT INTO substances(name,created_at,updated_at,deleted_at) VALUES('ALCOHOL',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('ANFETAMINA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('COCAINA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('CRACK',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('EXTASIS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('HIPNOTICOS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('INHALABLES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('LSD',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('MARIHUANA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('METAFETAMINAS Y OTROS DERIVADOS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('OTROS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('OTROS ALUCINOGENOS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('OTROS ESTIMULANTES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('OTROS OPIOIDES ANALGESICOS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('PASTA BASE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('TRANQUILIZANTES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('SEDANTES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('SIN ESPECIFICAR',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('TUSSI/KETAMINA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('FENTANILO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);


INSERT INTO senders(name,created_at,updated_at,deleted_at) VALUES('USUARIO DIRECTAMENTE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                        ('FAMILIAR',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                        ('SECTOR JUSTICIO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                        ('OTRO CENTRO DE SALUD',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                        ('OTRO CENTRO DE TRATAMIENTO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('PREVIENE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('EMPRESA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('SERVICIOS SOCIALES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('COLEGIO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('OTROS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);


INSERT INTO diverters(name,created_at,updated_at,deleted_at) VALUES('DEMANDA ESPONTANEA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('CENTRO DE SALUD',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('CENTRO DE TRATAMIENTO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('JUSTICIA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('EMPRESA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('COLEGIO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('SERVICIOS SOCIALES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('PREVIENE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                 ('OTROS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);


INSERT INTO not_relevants(name,created_at,updated_at,deleted_at) VALUES('POR PREVISION DE SALUD',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('JURISDICCION',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('DIAGNOSTICO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('OTROS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('NINGUNA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);



INSERT INTO int_prevs(name,created_at,updated_at,deleted_at) VALUES('Fonasa',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('ISAPRE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('CAPREDENA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Desconocido',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Particular',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Ninguna',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);


INSERT INTO conv_prevs(name,int_prev_id,created_at,updated_at,deleted_at) VALUES('Fonasa A',1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Fonasa B',1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Fonasa C',1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Fonasa D',1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Fonasa E',1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Banmedica',2,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Colmena Golden Cross',2,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Capredena',3,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Particular',5,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Desconocido',4,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Ninguna',6,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);

INSERT INTO results(name,created_at,updated_at,deleted_at) VALUES('Ingreso a tratamiento',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Lista de espera	',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Referencia',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Historico',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                                ('Aun sin Resultado',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);

INSERT INTO professions(name,created_at,updated_at,deleted_at) VALUES('Psiquiatra',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                       ('Psicólogo',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);


INSERT INTO states(name,created_at,updated_at,deleted_at) VALUES('En Tramite',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                      ('Aceptado',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                      ('No Aceptado',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);

INSERT INTO contacts_types(name,created_at,updated_at,deleted_at) VALUES('PRESENCIAL',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                      ('TELEFONICA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                      ('CORREO POSTAL',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                      ('DIGITAL',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                      ('OTRO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);









