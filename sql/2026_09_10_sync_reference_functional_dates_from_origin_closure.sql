-- Sincroniza fechas funcionales de referencias desde el cierre formal de la etapa origen.
-- Objetivo: diferenciar fecha funcional (closed_at/reference_date/received_at/event_date)
--           de fecha de registro/auditoría (created_at), que NO se modifica.
-- Caso guía: DEM-000051, cierre funcional por REFERENCIA el 2026-04-16 y registro posterior en sistema.

START TRANSACTION;

-- 1) Referencia funcional = fecha de cierre funcional de la etapa origen cuando la causal/resultado es REFERENCIA.
UPDATE episode_references r
JOIN episode_stages os ON os.id = r.origin_stage_id
LEFT JOIN closure_reasons cr ON cr.id = os.closure_reason_id
SET r.reference_date = os.closed_at,
    r.updated_at = NOW(6)
WHERE r.deleted_at IS NULL
  AND os.deleted_at IS NULL
  AND os.closed_at IS NOT NULL
  AND UPPER(COALESCE(cr.code, os.result_code, '')) = 'REFERENCIA'
  AND (r.reference_date IS NULL OR r.reference_date <> os.closed_at);

-- 2) La etapa destino debe ingresar funcionalmente en la misma fecha de referencia.
UPDATE episode_stages ds
JOIN episode_references r ON r.destination_stage_id = ds.id
JOIN episode_stages os ON os.id = r.origin_stage_id
LEFT JOIN closure_reasons cr ON cr.id = os.closure_reason_id
SET ds.received_at = r.reference_date,
    ds.updated_at = NOW(6)
WHERE ds.deleted_at IS NULL
  AND r.deleted_at IS NULL
  AND os.deleted_at IS NULL
  AND os.closed_at IS NOT NULL
  AND UPPER(COALESCE(cr.code, os.result_code, '')) = 'REFERENCIA'
  AND r.reference_date IS NOT NULL
  AND (ds.received_at IS NULL OR ds.received_at <> r.reference_date);

-- 3) El evento REFERENCIA conserva created_at como registro/auditoría,
--    pero event_date/event_time representan la fecha funcional.
UPDATE episode_events ev
JOIN event_types et ON et.id = ev.event_type_id
JOIN episode_references r ON r.episode_id = ev.episode_id AND r.origin_stage_id = ev.stage_id
JOIN episode_stages os ON os.id = r.origin_stage_id
LEFT JOIN closure_reasons cr ON cr.id = os.closure_reason_id
SET ev.event_date = DATE(r.reference_date),
    ev.event_time = TIME(r.reference_date),
    ev.updated_at = NOW(6)
WHERE ev.deleted_at IS NULL
  AND r.deleted_at IS NULL
  AND os.deleted_at IS NULL
  AND UPPER(et.code) = 'REFERENCIA'
  AND os.closed_at IS NOT NULL
  AND UPPER(COALESCE(cr.code, os.result_code, '')) = 'REFERENCIA'
  AND r.reference_date IS NOT NULL
  AND (ev.event_date <> DATE(r.reference_date) OR ev.event_time <> TIME(r.reference_date));

-- 4) Los eventos CIERRE también deben mostrar event_date/event_time funcionales,
--    manteniendo created_at como fecha real de registro.
UPDATE episode_events ev
JOIN event_types et ON et.id = ev.event_type_id
JOIN episode_stages s ON s.id = ev.stage_id
SET ev.event_date = DATE(s.closed_at),
    ev.event_time = TIME(s.closed_at),
    ev.updated_at = NOW(6)
WHERE ev.deleted_at IS NULL
  AND s.deleted_at IS NULL
  AND UPPER(et.code) = 'CIERRE'
  AND s.closed_at IS NOT NULL
  AND (ev.event_date <> DATE(s.closed_at) OR ev.event_time <> TIME(s.closed_at));

COMMIT;
