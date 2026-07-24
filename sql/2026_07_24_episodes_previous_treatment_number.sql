-- Ajuste requerido para migrar el dato histórico del sistema anterior:
-- registers.number_tto -> episodes.previous_treatment_number
-- Ejecutar una sola vez antes de levantar el backend con esta versión.
-- Script idempotente para MySQL 8.x.

SET @database_name = DATABASE();

-- 1) Crear columna si no existe.
SET @column_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @database_name
    AND TABLE_NAME = 'episodes'
    AND COLUMN_NAME = 'previous_treatment_number'
);

SET @sql = IF(
  @column_exists = 0,
  'ALTER TABLE episodes ADD COLUMN previous_treatment_number INT NOT NULL DEFAULT 0 AFTER episode_type_id',
  'SELECT ''La columna episodes.previous_treatment_number ya existe'' AS info'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) Normalizar datos existentes.
UPDATE episodes
SET previous_treatment_number = 0
WHERE previous_treatment_number IS NULL OR previous_treatment_number < 0;

-- 3) Asegurar valor por defecto 0 y NOT NULL.
ALTER TABLE episodes
  MODIFY COLUMN previous_treatment_number INT NOT NULL DEFAULT 0;

-- 4) Crear CHECK si no existe.
SET @check_exists = (
  SELECT COUNT(*)
  FROM information_schema.TABLE_CONSTRAINTS
  WHERE TABLE_SCHEMA = @database_name
    AND TABLE_NAME = 'episodes'
    AND CONSTRAINT_NAME = 'chk_episodes_previous_treatment_number'
    AND CONSTRAINT_TYPE = 'CHECK'
);

SET @sql = IF(
  @check_exists = 0,
  'ALTER TABLE episodes ADD CONSTRAINT chk_episodes_previous_treatment_number CHECK (previous_treatment_number >= 0)',
  'SELECT ''El CHECK chk_episodes_previous_treatment_number ya existe'' AS info'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5) Validación.
SHOW COLUMNS FROM episodes LIKE 'previous_treatment_number';

SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE
FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'episodes'
  AND CONSTRAINT_NAME = 'chk_episodes_previous_treatment_number';
