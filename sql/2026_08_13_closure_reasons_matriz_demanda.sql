-- Ajuste catálogo closure_reasons según matriz vigente Gestión de Demanda.
-- Causales activas: REFERENCIA, INGRESO_TRATAMIENTO y ABANDONO.
-- Las causales históricas se conservan para trazabilidad, pero quedan inactivas.

INSERT INTO closure_reasons (code, name, description, active, created_at, updated_at, deleted_at)
VALUES
  ('REFERENCIA', 'Referencia', 'Cierre por referencia a otro programa o dispositivo.', 1, NOW(6), NOW(6), NULL),
  ('INGRESO_TRATAMIENTO', 'Ingreso a tratamiento', 'Cierre por ingreso efectivo a tratamiento.', 1, NOW(6), NOW(6), NULL),
  ('ABANDONO', 'Abandono', 'Cierre por abandono formal.', 1, NOW(6), NOW(6), NULL)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  description = VALUES(description),
  active = 1,
  deleted_at = NULL,
  updated_at = NOW(6);

UPDATE closure_reasons
SET active = 0,
    updated_at = NOW(6)
WHERE deleted_at IS NULL
  AND UPPER(code) NOT IN ('REFERENCIA', 'INGRESO_TRATAMIENTO', 'ABANDONO');
