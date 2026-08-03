-- ==========================================================
-- Gestión de Demanda - Catálogos de citación y compromiso biopsicosocial
-- Fecha: 2026-07-27
-- ==========================================================
-- Crea:
--   citation_types
--   biopsychosocial_commitment_levels
-- Modifica:
--   episode_events.citation_type_id
--   episode_events.biopsychosocial_commitment_level_id
-- ==========================================================

CREATE TABLE IF NOT EXISTS citation_types (
    id INT NOT NULL AUTO_INCREMENT,
    code VARCHAR(120) NOT NULL,
    name VARCHAR(180) NOT NULL,
    sort_order INT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_citation_types_code (code),
    KEY idx_citation_types_active_sort (active, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO citation_types (code, name, sort_order, active) VALUES
('PRIMERA_CITACION_PRIMERA_ENTREVISTA', 'Primera citación a primera entrevista.', 1, 1),
('SEGUNDA_CITACION_PRIMERA_ENTREVISTA', 'Segunda citación a primera entrevista.', 2, 1),
('PRIMERA_CITACION_SEGUNDA_ENTREVISTA', 'Primera citación a segunda entrevista.', 3, 1),
('SEGUNDA_CITACION_SEGUNDA_ENTREVISTA', 'Segunda citación a segunda entrevista.', 4, 1),
('PRIMERA_CITACION_TERCERA_ENTREVISTA', 'Primera citación a tercera entrevista.', 5, 1),
('SEGUNDA_CITACION_TERCERA_ENTREVISTA', 'Segunda citación a tercera entrevista.', 6, 1),
('ENTREVISTA_OPCIONAL', 'Entrevista opcional.', 7, 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    sort_order = VALUES(sort_order),
    active = 1;

CREATE TABLE IF NOT EXISTS biopsychosocial_commitment_levels (
    id INT NOT NULL AUTO_INCREMENT,
    code VARCHAR(80) NOT NULL,
    name VARCHAR(120) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_biopsychosocial_commitment_levels_code (code),
    KEY idx_biopsychosocial_commitment_levels_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO biopsychosocial_commitment_levels (code, name, active) VALUES
('LEVE', 'Leve', 1),
('MODERADO', 'Moderado', 1),
('SEVERO', 'Severo', 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    active = 1;

DELIMITER //

DROP PROCEDURE IF EXISTS gd_add_column_if_not_exists //
CREATE PROCEDURE gd_add_column_if_not_exists(
    IN p_table_name VARCHAR(128),
    IN p_column_name VARCHAR(128),
    IN p_column_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
          AND COLUMN_NAME = p_column_name
    ) THEN
        SET @sql = CONCAT('ALTER TABLE ', p_table_name, ' ADD COLUMN ', p_column_name, ' ', p_column_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //

DROP PROCEDURE IF EXISTS gd_add_index_if_not_exists //
CREATE PROCEDURE gd_add_index_if_not_exists(
    IN p_table_name VARCHAR(128),
    IN p_index_name VARCHAR(128),
    IN p_index_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
          AND INDEX_NAME = p_index_name
    ) THEN
        SET @sql = CONCAT('ALTER TABLE ', p_table_name, ' ADD INDEX ', p_index_name, ' ', p_index_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //

DROP PROCEDURE IF EXISTS gd_add_fk_if_not_exists //
CREATE PROCEDURE gd_add_fk_if_not_exists(
    IN p_table_name VARCHAR(128),
    IN p_column_name VARCHAR(128),
    IN p_constraint_name VARCHAR(128),
    IN p_referenced_table_name VARCHAR(128),
    IN p_referenced_column_name VARCHAR(128)
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
          AND COLUMN_NAME = p_column_name
          AND REFERENCED_TABLE_NAME = p_referenced_table_name
          AND REFERENCED_COLUMN_NAME = p_referenced_column_name
    ) THEN
        SET @sql = CONCAT(
            'ALTER TABLE ', p_table_name,
            ' ADD CONSTRAINT ', p_constraint_name,
            ' FOREIGN KEY (', p_column_name, ') REFERENCES ', p_referenced_table_name, '(', p_referenced_column_name, ')'
        );
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //

DELIMITER ;

CALL gd_add_column_if_not_exists('episode_events', 'citation_type_id', 'INT NULL');
CALL gd_add_column_if_not_exists('episode_events', 'biopsychosocial_commitment_level_id', 'INT NULL');

CALL gd_add_index_if_not_exists('episode_events', 'idx_event_citation_type', '(citation_type_id)');
CALL gd_add_index_if_not_exists('episode_events', 'idx_event_biopsychosocial_commitment_level', '(biopsychosocial_commitment_level_id)');

CALL gd_add_fk_if_not_exists('episode_events', 'citation_type_id', 'fk_episode_events_citation_type', 'citation_types', 'id');
CALL gd_add_fk_if_not_exists('episode_events', 'biopsychosocial_commitment_level_id', 'fk_episode_events_biopsychosocial_commitment_level', 'biopsychosocial_commitment_levels', 'id');

DROP PROCEDURE IF EXISTS gd_add_fk_if_not_exists;
DROP PROCEDURE IF EXISTS gd_add_index_if_not_exists;
DROP PROCEDURE IF EXISTS gd_add_column_if_not_exists;
