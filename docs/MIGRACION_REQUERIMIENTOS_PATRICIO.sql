-- MIGRACION REQUERIMIENTOS PATRICIO JARA
-- Sistema Demanda Tratamiento Drogas
-- MySQL 8.x / MariaDB 10.x
-- Recomendación: ejecutar en ambiente de pruebas antes de producción.
-- Si se usa spring.jpa.hibernate.ddl-auto=update en desarrollo, Hibernate puede crear parte del modelo automáticamente,
-- pero en producción se recomienda controlar estos cambios mediante migración SQL versionada.

-- =========================================================
-- 1. CATALOGOS NUEVOS
-- =========================================================

CREATE TABLE IF NOT EXISTS episode_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS event_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS attendance_statuses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS closure_reasons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS program_populations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS program_modalities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS program_plans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS regions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS cities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    region_id INT,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME,
    CONSTRAINT fk_cities_regions FOREIGN KEY (region_id) REFERENCES regions(id)
);

CREATE TABLE IF NOT EXISTS semaphore_rules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    color_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100),
    min_days INT NOT NULL,
    max_days INT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

-- =========================================================
-- 2. TABLAS NUEVAS DE EPISODIO / ETAPAS / AUDITORIA
-- =========================================================

CREATE TABLE IF NOT EXISTS episodes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    episode_code VARCHAR(30) UNIQUE,
    postulant_id INT NOT NULL,
    episode_type_id INT,
    original_request_date DATE NOT NULL,
    initial_program_id INT NOT NULL,
    current_program_id INT,
    current_stage_id INT,
    contact_type_id INT,
    sender_id INT,
    diverter_id INT,
    contact_id INT,
    state_code VARCHAR(60) DEFAULT 'EN_TRAMITE',
    result_code VARCHAR(80) DEFAULT 'AUN_SIN_RESULTADO',
    entry_to_treatment_at DATETIME,
    egress_at DATETIME,
    closed_at DATETIME,
    closure_reason_id INT,
    closure_comment VARCHAR(1200),
    active BOOLEAN DEFAULT TRUE,
    waiting_stopped BOOLEAN DEFAULT FALSE,
    created_by_user_id INT,
    closed_by_user_id INT,
    reversed_by_user_id INT,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME,
    CONSTRAINT fk_episodes_postulants FOREIGN KEY (postulant_id) REFERENCES postulants(id),
    CONSTRAINT fk_episodes_episode_types FOREIGN KEY (episode_type_id) REFERENCES episode_types(id),
    CONSTRAINT fk_episodes_initial_program FOREIGN KEY (initial_program_id) REFERENCES programs(id),
    CONSTRAINT fk_episodes_current_program FOREIGN KEY (current_program_id) REFERENCES programs(id),
    CONSTRAINT fk_episodes_contact_type FOREIGN KEY (contact_type_id) REFERENCES contacts_types(id),
    CONSTRAINT fk_episodes_sender FOREIGN KEY (sender_id) REFERENCES senders(id),
    CONSTRAINT fk_episodes_diverter FOREIGN KEY (diverter_id) REFERENCES diverters(id),
    CONSTRAINT fk_episodes_contact FOREIGN KEY (contact_id) REFERENCES contacts(id),
    CONSTRAINT fk_episodes_closure_reason FOREIGN KEY (closure_reason_id) REFERENCES closure_reasons(id),
    CONSTRAINT fk_episodes_created_by FOREIGN KEY (created_by_user_id) REFERENCES users(id),
    CONSTRAINT fk_episodes_closed_by FOREIGN KEY (closed_by_user_id) REFERENCES users(id),
    CONSTRAINT fk_episodes_reversed_by FOREIGN KEY (reversed_by_user_id) REFERENCES users(id),
    INDEX idx_episode_postulant_active (postulant_id, active),
    INDEX idx_episode_original_request_date (original_request_date),
    INDEX idx_episode_current_program (current_program_id)
);

CREATE TABLE IF NOT EXISTS episode_stages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    episode_id INT NOT NULL,
    program_id INT NOT NULL,
    stage_order INT,
    origin_stage_id INT,
    received_at DATETIME,
    closed_at DATETIME,
    state_code VARCHAR(60) DEFAULT 'EN_TRAMITE',
    result_code VARCHAR(80) DEFAULT 'AUN_SIN_RESULTADO',
    closure_reason_id INT,
    closure_comment VARCHAR(1200),
    is_current BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME,
    CONSTRAINT fk_stages_episodes FOREIGN KEY (episode_id) REFERENCES episodes(id),
    CONSTRAINT fk_stages_programs FOREIGN KEY (program_id) REFERENCES programs(id),
    CONSTRAINT fk_stages_origin_stage FOREIGN KEY (origin_stage_id) REFERENCES episode_stages(id),
    CONSTRAINT fk_stages_closure_reason FOREIGN KEY (closure_reason_id) REFERENCES closure_reasons(id),
    INDEX idx_stage_episode (episode_id),
    INDEX idx_stage_program_current (program_id, is_current)
);

ALTER TABLE episodes
    ADD CONSTRAINT fk_episodes_current_stage FOREIGN KEY (current_stage_id) REFERENCES episode_stages(id);

CREATE TABLE IF NOT EXISTS episode_events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    episode_id INT NOT NULL,
    stage_id INT,
    event_type_id INT NOT NULL,
    event_date DATE,
    event_time TIME,
    attendance_status_id INT,
    profession_id INT,
    professional_user_id INT,
    registered_by_user_id INT,
    program_id INT,
    comment VARCHAR(2000),
    citation_comment VARCHAR(1200),
    general_observation VARCHAR(2000),
    next_action VARCHAR(1200),
    next_action_date DATETIME,
    result_code VARCHAR(80),
    state_code VARCHAR(60),
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME,
    CONSTRAINT fk_events_episodes FOREIGN KEY (episode_id) REFERENCES episodes(id),
    CONSTRAINT fk_events_stages FOREIGN KEY (stage_id) REFERENCES episode_stages(id),
    CONSTRAINT fk_events_event_types FOREIGN KEY (event_type_id) REFERENCES event_types(id),
    CONSTRAINT fk_events_attendance FOREIGN KEY (attendance_status_id) REFERENCES attendance_statuses(id),
    CONSTRAINT fk_events_profession FOREIGN KEY (profession_id) REFERENCES professions(id),
    CONSTRAINT fk_events_professional_user FOREIGN KEY (professional_user_id) REFERENCES users(id),
    CONSTRAINT fk_events_registered_by FOREIGN KEY (registered_by_user_id) REFERENCES users(id),
    CONSTRAINT fk_events_program FOREIGN KEY (program_id) REFERENCES programs(id),
    INDEX idx_event_episode_date (episode_id, event_date),
    INDEX idx_event_stage (stage_id)
);

CREATE TABLE IF NOT EXISTS episode_references (
    id INT AUTO_INCREMENT PRIMARY KEY,
    episode_id INT NOT NULL,
    origin_stage_id INT,
    destination_stage_id INT,
    origin_program_id INT,
    destination_program_id INT NOT NULL,
    reference_date DATETIME,
    reason VARCHAR(1200),
    observation VARCHAR(2000),
    document_id INT,
    created_by_user_id INT,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME,
    CONSTRAINT fk_references_episode FOREIGN KEY (episode_id) REFERENCES episodes(id),
    CONSTRAINT fk_references_origin_stage FOREIGN KEY (origin_stage_id) REFERENCES episode_stages(id),
    CONSTRAINT fk_references_destination_stage FOREIGN KEY (destination_stage_id) REFERENCES episode_stages(id),
    CONSTRAINT fk_references_origin_program FOREIGN KEY (origin_program_id) REFERENCES programs(id),
    CONSTRAINT fk_references_destination_program FOREIGN KEY (destination_program_id) REFERENCES programs(id),
    CONSTRAINT fk_references_created_by FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS episode_audit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    episode_id INT,
    stage_id INT,
    event_id INT,
    action_type VARCHAR(100),
    previous_value VARCHAR(2000),
    new_value VARCHAR(2000),
    reason VARCHAR(2000),
    performed_by_user_id INT,
    authorized_by_user_id INT,
    reversed_by_user_id INT,
    performed_at DATETIME,
    reversed_at DATETIME,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME,
    CONSTRAINT fk_audit_episode FOREIGN KEY (episode_id) REFERENCES episodes(id),
    CONSTRAINT fk_audit_stage FOREIGN KEY (stage_id) REFERENCES episode_stages(id),
    CONSTRAINT fk_audit_event FOREIGN KEY (event_id) REFERENCES episode_events(id),
    CONSTRAINT fk_audit_performed_by FOREIGN KEY (performed_by_user_id) REFERENCES users(id),
    CONSTRAINT fk_audit_authorized_by FOREIGN KEY (authorized_by_user_id) REFERENCES users(id),
    CONSTRAINT fk_audit_reversed_by FOREIGN KEY (reversed_by_user_id) REFERENCES users(id)
);

-- =========================================================
-- 3. MODIFICACIONES A TABLAS EXISTENTES
-- =========================================================

ALTER TABLE programs
    ADD COLUMN population_type_id INT NULL,
    ADD COLUMN modality_id INT NULL,
    ADD COLUMN plan_id INT NULL,
    ADD COLUMN region_id INT NULL,
    ADD COLUMN city_id INT NULL,
    ADD COLUMN address VARCHAR(255) NULL,
    ADD COLUMN phone VARCHAR(255) NULL,
    ADD COLUMN email VARCHAR(255) NULL,
    ADD COLUMN description VARCHAR(1000) NULL,
    ADD COLUMN active BOOLEAN DEFAULT TRUE;

-- Renombrar si existen columnas antiguas.
-- Ajustar manualmente si la columna no existe en su base.
-- ALTER TABLE programs CHANGE COLUMN cellphone phone VARCHAR(255);
-- ALTER TABLE programs CHANGE COLUMN city city_id INT;

ALTER TABLE programs
    ADD CONSTRAINT fk_program_population FOREIGN KEY (population_type_id) REFERENCES program_populations(id),
    ADD CONSTRAINT fk_program_modality FOREIGN KEY (modality_id) REFERENCES program_modalities(id),
    ADD CONSTRAINT fk_program_plan FOREIGN KEY (plan_id) REFERENCES program_plans(id),
    ADD CONSTRAINT fk_program_region FOREIGN KEY (region_id) REFERENCES regions(id),
    ADD CONSTRAINT fk_program_city FOREIGN KEY (city_id) REFERENCES cities(id);

-- Campos ambiguos a revisar/eliminar después de validar que no se usen:
-- ALTER TABLE programs DROP COLUMN type;
-- ALTER TABLE programs DROP COLUMN c1;
-- ALTER TABLE programs DROP COLUMN c2;

ALTER TABLE postulants
    ADD COLUMN first_last_name VARCHAR(255) NULL,
    ADD COLUMN second_last_name VARCHAR(255) NULL,
    ADD COLUMN birthdate VARCHAR(255) NULL,
    ADD COLUMN email VARCHAR(255) NULL,
    ADD COLUMN phone VARCHAR(255) NULL,
    ADD COLUMN address VARCHAR(255) NULL,
    ADD COLUMN commune_id INT NULL;

CREATE UNIQUE INDEX ux_postulants_rut ON postulants(rut);

CREATE TABLE IF NOT EXISTS states (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    code VARCHAR(255) UNIQUE,
    scope VARCHAR(255),
    description VARCHAR(1000),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME
);

ALTER TABLE results
    ADD COLUMN code VARCHAR(255) NULL,
    ADD COLUMN scope VARCHAR(255) NULL,
    ADD COLUMN description VARCHAR(1000) NULL,
    ADD COLUMN active BOOLEAN DEFAULT TRUE;

ALTER TABLE users_programs
    ADD COLUMN is_active BOOLEAN DEFAULT TRUE,
    ADD COLUMN is_supervisor BOOLEAN DEFAULT FALSE,
    ADD COLUMN can_receive_references BOOLEAN DEFAULT FALSE,
    ADD COLUMN can_manage_demands BOOLEAN DEFAULT FALSE,
    ADD COLUMN can_view_dashboard BOOLEAN DEFAULT FALSE,
    ADD COLUMN role_in_program VARCHAR(255) NULL;

ALTER TABLE roles
    ADD COLUMN code VARCHAR(255) NULL,
    ADD COLUMN description VARCHAR(1000) NULL,
    ADD COLUMN active BOOLEAN DEFAULT TRUE;

ALTER TABLE users_roles
    ADD COLUMN active BOOLEAN DEFAULT TRUE,
    ADD COLUMN assigned_by_user_id INT NULL,
    ADD CONSTRAINT fk_users_roles_assigned_by FOREIGN KEY (assigned_by_user_id) REFERENCES users(id);

-- =========================================================
-- 4. REGLAS A CONTROLAR DESDE BACKEND
-- =========================================================
-- 1) Una persona puede tener muchos episodios históricos.
-- 2) Solo puede existir un episodio activo por persona.
-- 3) Las referencias no crean episodio nuevo; cierran etapa origen y crean etapa destino.
-- 4) El ingreso efectivo a tratamiento detiene KPI de espera.
-- 5) Egreso y cierre quedan auditados y solo se revierten por perfil autorizado.
