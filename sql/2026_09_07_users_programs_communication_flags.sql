-- ============================================================================
-- Gestión de Demanda - Comunicaciones automáticas por usuario/programa
-- Fecha: 2026-09-07
-- Ajuste: agrega flags independientes en users_programs para Directorio y comunicaciones.
-- Nota: can_receive_references ya existía y no se modifica.
-- ============================================================================

SET @schema_name = DATABASE();

SET @sql = IF(
    EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'users_programs' AND COLUMN_NAME = 'can_manage_communications'),
    'SELECT ''can_manage_communications already exists''',
    'ALTER TABLE users_programs ADD COLUMN can_manage_communications TINYINT(1) NOT NULL DEFAULT 0 AFTER can_receive_references'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'users_programs' AND COLUMN_NAME = 'can_receive_citations'),
    'SELECT ''can_receive_citations already exists''',
    'ALTER TABLE users_programs ADD COLUMN can_receive_citations TINYINT(1) NOT NULL DEFAULT 0 AFTER can_manage_communications'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'users_programs' AND COLUMN_NAME = 'can_receive_attendances'),
    'SELECT ''can_receive_attendances already exists''',
    'ALTER TABLE users_programs ADD COLUMN can_receive_attendances TINYINT(1) NOT NULL DEFAULT 0 AFTER can_receive_citations'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'users_programs' AND COLUMN_NAME = 'can_receive_feedback'),
    'SELECT ''can_receive_feedback already exists''',
    'ALTER TABLE users_programs ADD COLUMN can_receive_feedback TINYINT(1) NOT NULL DEFAULT 0 AFTER can_receive_attendances'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'users_programs' AND COLUMN_NAME = 'can_receive_closures'),
    'SELECT ''can_receive_closures already exists''',
    'ALTER TABLE users_programs ADD COLUMN can_receive_closures TINYINT(1) NOT NULL DEFAULT 0 AFTER can_receive_feedback'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'users_programs' AND COLUMN_NAME = 'can_receive_documents'),
    'SELECT ''can_receive_documents already exists''',
    'ALTER TABLE users_programs ADD COLUMN can_receive_documents TINYINT(1) NOT NULL DEFAULT 0 AFTER can_receive_closures'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'users_programs' AND COLUMN_NAME = 'can_receive_observations'),
    'SELECT ''can_receive_observations already exists''',
    'ALTER TABLE users_programs ADD COLUMN can_receive_observations TINYINT(1) NOT NULL DEFAULT 0 AFTER can_receive_documents'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Normaliza valores nulos en instalaciones existentes.
UPDATE users_programs
SET
    can_manage_communications = COALESCE(can_manage_communications, 0),
    can_receive_citations = COALESCE(can_receive_citations, 0),
    can_receive_attendances = COALESCE(can_receive_attendances, 0),
    can_receive_feedback = COALESCE(can_receive_feedback, 0),
    can_receive_closures = COALESCE(can_receive_closures, 0),
    can_receive_documents = COALESCE(can_receive_documents, 0),
    can_receive_observations = COALESCE(can_receive_observations, 0),
    updated_at = NOW(6)
WHERE deleted_at IS NULL;

-- Validación:
SELECT
    id,
    user_id,
    program_id,
    can_receive_references,
    can_manage_communications,
    can_receive_citations,
    can_receive_attendances,
    can_receive_feedback,
    can_receive_closures,
    can_receive_documents,
    can_receive_observations
FROM users_programs
ORDER BY id
LIMIT 20;
