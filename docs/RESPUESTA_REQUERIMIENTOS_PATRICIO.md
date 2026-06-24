# Respuesta técnica a requerimientos Patricio Jara

Se incorporan los requerimientos solicitados al backend rediseñado de Gestión de Demanda de Tratamiento de Drogas.

## Tablas nuevas consideradas

1. `episode_types`
2. `event_types`
3. `attendance_statuses`
4. `closure_reasons`
5. `program_populations`
6. `program_modalities`
7. `program_plans`
8. `semaphore_rules`
9. `regions`
10. `cities`
11. `episode_stages`
12. `episode_audit_logs`

Además, el rediseño conserva las tablas complementarias ya propuestas para operación completa:

- `episodes`
- `episode_events`
- `episode_references`
- `episode_documents`
- `episode_alerts`
- `episode_substances`

## Programs

Se modifica `programs` agregando:

- `population_type_id`
- `modality_id`
- `plan_id`
- `region_id`
- `city_id`
- `address`
- `phone`
- `email`
- `description`
- `active`

Se deja representado el cambio funcional:

- `cellphone` pasa a `phone`
- `city` pasa a `city_id`

Los campos `type`, `c1` y `c2` deben quedar obsoletos o ser eliminados solo después de confirmar que no se usan en producción.

## Postulants

Se asegura el modelo base de persona:

- `rut` único
- `first_name`
- `first_last_name`
- `second_last_name`
- `birthdate`
- `sex_id`
- `email`
- `phone`
- `address`
- `commune_id`

Regla funcional aplicada en servicio:

- Una persona puede tener múltiples episodios históricos.
- Solo se permite un episodio activo por persona.

## Tablas a modificar

### states

Se agrega entidad, repositorio, servicio y controlador para `states`.

Campos:

- `id`
- `name`
- `code`
- `scope`
- `description`
- `active`
- `created_at`
- `updated_at`
- `deleted_at`

Endpoint administrativo:

```text
/api/v1/states
```

### results

Se agregan:

- `code`
- `scope`
- `description`
- `active`

Se mantienen:

- `id`
- `name`
- `created_at`
- `updated_at`
- `deleted_at`

### users_programs

Se agregan:

- `is_active`
- `is_supervisor`
- `can_receive_references`
- `can_manage_demands`
- `can_view_dashboard`
- `role_in_program`

Se mantienen:

- `id`
- `user_id`
- `program_id`
- `created_at`
- `updated_at`
- `deleted_at`

### roles

Se agregan:

- `code`
- `description`
- `active`

Se mantienen:

- `id`
- `name`
- `created_at`
- `updated_at`
- `deleted_at`

### users_roles

Se agregan:

- `active`
- `assigned_by_user_id`

Se mantienen:

- `id`
- `user_id`
- `role_id`
- `created_at`
- `updated_at`
- `deleted_at`

## Archivos relevantes modificados

- `entity/StateEntity.java`
- `dto/StateDTO.java`
- `repository/StateRepository.java`
- `service/IStateService.java`
- `service/impl/StateServiceImpl.java`
- `controller/StateController.java`
- `entity/ProgramEntity.java`
- `entity/PostulantEntity.java`
- `entity/ResultEntity.java`
- `entity/RoleEntity.java`
- `entity/UserProgramEntity.java`
- `entity/UserRoleEntity.java`
- DTO y servicios asociados.

## Migración SQL

Se deja archivo sugerido:

```text
docs/MIGRACION_REQUERIMIENTOS_PATRICIO.sql
```

