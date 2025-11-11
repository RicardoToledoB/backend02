-- Región de Arica y Parinacota (XV)
INSERT INTO communes (name, created_at, updated_at, deleted_at) VALUES
                                                                    ('Arica', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Camarones', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Putre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('General Lagos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

-- Región de Tarapacá (I)
INSERT INTO communes (name, created_at, updated_at, deleted_at) VALUES
                                                                    ('Iquique', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Alto Hospicio', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Pozo Almonte', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Camiña', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Colchane', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Huara', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Pica', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

-- Región de Antofagasta (II)
INSERT INTO communes (name, created_at, updated_at, deleted_at) VALUES
                                                                    ('Antofagasta', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Mejillones', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Sierra Gorda', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Taltal', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Calama', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Ollagüe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('San Pedro de Atacama', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Tocopilla', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('María Elena', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

-- Región de Atacama (III)
INSERT INTO communes (name, created_at, updated_at, deleted_at) VALUES
                                                                    ('Copiapó', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Caldera', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Tierra Amarilla', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Chañaral', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Diego de Almagro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Vallenar', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Alto del Carmen', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Freirina', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Huasco', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

-- Región de Coquimbo (IV)
INSERT INTO communes (name, created_at, updated_at, deleted_at) VALUES
                                                                    ('La Serena', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Coquimbo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Andacollo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('La Higuera', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Paiguano', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Vicuña', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Illapel', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Canela', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Los Vilos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Salamanca', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Ovalle', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Combarbalá', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Monte Patria', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Punitaqui', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Río Hurtado', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);


INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAB THOMAS FENTON',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAI ADULTO MAGALLANES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PROGRAMA RESIDENCIAL  MAGALLANES / PAI PR MIXTO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PROGRAMA KSORREM KANENNA /PAI PR MUJERES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAC PARA PERSONAS EN SITUACION DE CALLE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAI ADOLESCENTE PUNTA ARENAS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PROYECTO PAI INFRACTORES CAUDA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAI ADULTO NATALES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);
INSERT INTO programs(name,created_at,updated_at,deleted_at) VALUES('PAI ADOLESCENTE NATALES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);


INSERT INTO sexs(name,created_at,updated_at,deleted_at) VALUES('HOMBRE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('MUJER',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('INTERSEX',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('DESCONCIDO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('NO INFORMADO',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);


INSERT INTO substances(name,created_at,updated_at,deleted_at) VALUES('ALCOHOL',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('MARIHUANA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('COCAINA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('PASTABASE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('TRANQUILIZANTES O PASTILLAS PARA DORMIR',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('ANFETAMINAS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('OPIACEOS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('INHALANTES',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('ALUCINOGENOS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                              ('OTROS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);

INSERT INTO contacts_types(name,created_at,updated_at,deleted_at) VALUES('PRESENCIAL',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                    ('TELEFONICA',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                    ('CORREO POSTAL',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                    ('DIGITAL',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                    ('OTROS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);


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
                                                                   ('OTROS',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);



INSERT INTO int_prevs(name,created_at,updated_at,deleted_at) VALUES('Fonasa',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Banmedica S.A',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Capredena',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Chuquicamata Ltda.',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Cigna Salud',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Colmena Golden Cross S.A',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Consalud S.A',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Cooperativa de Servicios de Proteccion Medica',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Cruz del Norte Ltda',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Ctc Istel',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Dipreca',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Ferrosalud S.A',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Alemana Salud S.A',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Fusat Ltda',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Cruz Blanca S.A.(Ex ING)',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Isapre Fundacion',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Linksalud Vida',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Master Salud',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('MasVida S.A',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Nortmedica S.A',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Promepart',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Rio Blanco Ltda.',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('San Lorenzo Ltda.',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Sfera S.A.',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Vida Tres S.A',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Jeafosale',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Seguros obligatorios contra terceros',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Seguro escolar public',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Seguro escolar privado',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Desconocido',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Particular',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null),
                                                                   ('Ninguna',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null);














