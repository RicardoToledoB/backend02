-- Agrega tipos de citación para tercera entrevista.
-- Idempotente para MySQL 8.

INSERT INTO citation_types (code, name, sort_order, active) VALUES
('PRIMERA_CITACION_TERCERA_ENTREVISTA', 'Primera citación a tercera entrevista.', 5, 1),
('SEGUNDA_CITACION_TERCERA_ENTREVISTA', 'Segunda citación a tercera entrevista.', 6, 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    sort_order = VALUES(sort_order),
    active = 1;

UPDATE citation_types
SET sort_order = 7,
    active = 1
WHERE code = 'ENTREVISTA_OPCIONAL';
