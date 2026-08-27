-- ============================================================================
-- Gestión de Demanda - Catálogo compromiso biopsicosocial
-- Fecha: 2026-08-27
-- Ajuste: incorpora registro NO_EVALUADO - No fue evaluado
-- ============================================================================

CREATE TABLE IF NOT EXISTS biopsychosocial_commitment_levels (
    id INT NOT NULL AUTO_INCREMENT,
    code VARCHAR(80) NOT NULL,
    name VARCHAR(120) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_biopsychosocial_commitment_levels_code (code),
    KEY idx_biopsychosocial_commitment_levels_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO biopsychosocial_commitment_levels (code, name, active)
VALUES ('NO_EVALUADO', 'No fue evaluado', 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    active = 1;
