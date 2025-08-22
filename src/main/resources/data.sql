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












INSERT INTO communes (name, created_at, updated_at, deleted_at)
SELECT 'Commune ' || x, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
       CASE WHEN MOD(x, 20)=0 THEN CURRENT_TIMESTAMP ELSE NULL END
FROM SYSTEM_RANGE(1, 100000) AS t(x);

-- 100001..200000
INSERT INTO communes (name, created_at, updated_at, deleted_at)
SELECT 'Commune ' || x, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
       CASE WHEN MOD(x, 20)=0 THEN CURRENT_TIMESTAMP ELSE NULL END
FROM SYSTEM_RANGE(100001, 200000) AS t(x);