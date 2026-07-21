-- Ajuste requerido para asociar postulantes con convenio previsional.
-- Ejecutar antes de reiniciar el backend con esta versión.

-- 1) Verificar si la columna ya existe:
SHOW COLUMNS FROM postulants LIKE 'conv_prev_id';

-- 2) Si no existe, ejecutar:
ALTER TABLE postulants
  ADD COLUMN conv_prev_id INT NULL AFTER sex_id;

-- 3) Crear índice si no existe:
CREATE INDEX idx_postulants_conv_prev_id ON postulants (conv_prev_id);

-- 4) Crear FK si corresponde al ambiente:
ALTER TABLE postulants
  ADD CONSTRAINT fk_postulants_conv_prev
  FOREIGN KEY (conv_prev_id) REFERENCES conv_prevs(id);

-- 5) Ejemplo para asociar un postulante existente:
-- UPDATE postulants SET conv_prev_id = 1 WHERE id = 1;

-- 6) Validación:
SELECT p.id,
       p.rut,
       p.conv_prev_id,
       cp.code AS conv_prev_code,
       cp.name AS conv_prev_name,
       ip.code AS int_prev_code,
       ip.name AS int_prev_name
FROM postulants p
LEFT JOIN conv_prevs cp ON cp.id = p.conv_prev_id
LEFT JOIN int_prevs ip ON ip.id = cp.int_prev_id
WHERE p.id = 1;
