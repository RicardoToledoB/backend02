-- Sincroniza el catálogo histórico communes con el catálogo oficial cities.
-- Motivo: /api/v1/postulants usa postulants.commune_id, pero el frontend selecciona IDs desde
-- /api/v1/demand/maintainers/cities. Si el ID existe en cities y no en communes, Hibernate lanza 500.

INSERT INTO communes (id, name, created_at, updated_at, deleted_at)
SELECT
    c.id,
    c.name,
    COALESCE(c.created_at, NOW(6)),
    NOW(6),
    NULL
FROM cities c
WHERE c.deleted_at IS NULL
  AND (c.active IS NULL OR c.active = 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    updated_at = NOW(6),
    deleted_at = NULL;

-- Validación esperada para Porvenir:
SELECT id, name, deleted_at
FROM communes
WHERE id = 3;
