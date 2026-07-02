package com.cosam.project01.demand.service;

import com.cosam.project01.demand.dto.CatalogMaintainerDTO;
import com.cosam.project01.demand.dto.CatalogMaintainerRequest;
import com.cosam.project01.demand.entity.*;
import com.cosam.project01.demand.repository.*;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogMaintainerService {

    private final EpisodeTypeRepository episodeTypeRepository;
    private final EventTypeRepository eventTypeRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;
    private final ClosureReasonRepository closureReasonRepository;
    private final ProgramPopulationRepository programPopulationRepository;
    private final ProgramModalityRepository programModalityRepository;
    private final ProgramPlanRepository programPlanRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final SemaphoreRuleRepository semaphoreRuleRepository;
    private final EntityManager entityManager;

    public List<Map<String, String>> supportedCatalogs() {
        return List.of(
                item("episode-types", "episode_types"),
                item("event-types", "event_types"),
                item("attendance-statuses", "attendance_statuses"),
                item("closure-reasons", "closure_reasons"),
                item("program-populations", "program_populations"),
                item("program-modalities", "program_modalities"),
                item("program-plans", "program_plans"),
                item("document-types", "document_types"),
                item("regions", "regions"),
                item("cities", "cities"),
                item("semaphore-rules", "semaphore_rules")
        );
    }

    private Map<String, String> item(String endpoint, String tableName) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("endpoint", endpoint);
        map.put("tableName", tableName);
        return map;
    }

    @Transactional(readOnly = true)
    public List<CatalogMaintainerDTO> list(String catalog, String q, Boolean active) {
        List<?> items = repositoryFor(catalog).findAll();
        return items.stream()
                .map(this::toDTO)
                .filter(dto -> active == null || Objects.equals(dto.getActive(), active))
                .filter(dto -> matches(dto, q))
                .sorted(Comparator.comparing(CatalogMaintainerDTO::getId, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CatalogMaintainerDTO> listPaginated(String catalog, String q, Boolean active, Pageable pageable) {
        List<CatalogMaintainerDTO> filtered = list(catalog, q, active);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<CatalogMaintainerDTO> pageContent = start > end ? List.of() : filtered.subList(start, end);
        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    @Transactional(readOnly = true)
    public CatalogMaintainerDTO getById(String catalog, Integer id) {
        Object entity = repositoryFor(catalog).findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado"));
        return toDTO(entity);
    }

    @Transactional
    public CatalogMaintainerDTO create(String catalog, CatalogMaintainerRequest request) {
        Object entity = newEntity(catalog);
        applyRequest(catalog, entity, request, false);
        Object saved = repositoryFor(catalog).save(entity);
        return toDTO(saved);
    }

    @Transactional
    public CatalogMaintainerDTO update(String catalog, Integer id, CatalogMaintainerRequest request) {
        Object entity = repositoryFor(catalog).findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado"));
        applyRequest(catalog, entity, request, true);
        Object saved = repositoryFor(catalog).save(entity);
        return toDTO(saved);
    }

    @Transactional
    public void delete(String catalog, Integer id) {
        JpaRepository repository = repositoryFor(catalog);
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void restore(String catalog, Integer id) {
        String table = tableNameFor(catalog);
        int updated = entityManager
                .createNativeQuery("UPDATE " + table + " SET deleted_at = NULL, active = TRUE WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado para restaurar");
        }
    }

    private boolean matches(CatalogMaintainerDTO dto, String q) {
        if (q == null || q.trim().isEmpty()) return true;
        String term = q.trim().toLowerCase();
        return contains(dto.getCode(), term)
                || contains(dto.getName(), term)
                || contains(dto.getDescription(), term)
                || contains(dto.getColorCode(), term)
                || contains(dto.getRegionCode(), term)
                || contains(dto.getRegionName(), term);
    }

    private boolean contains(String value, String term) {
        return value != null && value.toLowerCase().contains(term);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private JpaRepository<Object, Integer> repositoryFor(String catalog) {
        JpaRepository repository = switch (normalize(catalog)) {
            case "episode-types" -> episodeTypeRepository;
            case "event-types" -> eventTypeRepository;
            case "attendance-statuses" -> attendanceStatusRepository;
            case "closure-reasons" -> closureReasonRepository;
            case "program-populations" -> programPopulationRepository;
            case "program-modalities" -> programModalityRepository;
            case "program-plans" -> programPlanRepository;
            case "document-types" -> documentTypeRepository;
            case "regions" -> regionRepository;
            case "cities" -> cityRepository;
            case "semaphore-rules" -> semaphoreRuleRepository;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Catálogo no soportado: " + catalog);
        };
        return (JpaRepository<Object, Integer>) repository;
    }

    private Object newEntity(String catalog) {
        return switch (normalize(catalog)) {
            case "episode-types" -> new EpisodeTypeEntity();
            case "event-types" -> new EventTypeEntity();
            case "attendance-statuses" -> new AttendanceStatusEntity();
            case "closure-reasons" -> new ClosureReasonEntity();
            case "program-populations" -> new ProgramPopulationEntity();
            case "program-modalities" -> new ProgramModalityEntity();
            case "program-plans" -> new ProgramPlanEntity();
            case "document-types" -> new DocumentTypeEntity();
            case "regions" -> new RegionEntity();
            case "cities" -> new CityEntity();
            case "semaphore-rules" -> new SemaphoreRuleEntity();
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Catálogo no soportado: " + catalog);
        };
    }

    private String tableNameFor(String catalog) {
        return switch (normalize(catalog)) {
            case "episode-types" -> "episode_types";
            case "event-types" -> "event_types";
            case "attendance-statuses" -> "attendance_statuses";
            case "closure-reasons" -> "closure_reasons";
            case "program-populations" -> "program_populations";
            case "program-modalities" -> "program_modalities";
            case "program-plans" -> "program_plans";
            case "document-types" -> "document_types";
            case "regions" -> "regions";
            case "cities" -> "cities";
            case "semaphore-rules" -> "semaphore_rules";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Catálogo no soportado: " + catalog);
        };
    }

    private String normalize(String catalog) {
        if (catalog == null) return "";

        // Soporta las variantes usadas por frontend y backend:
        // episode-types, episode_types, episodeTypes, EpisodeTypes, etc.
        String value = catalog.trim()
                .replaceAll("([a-z0-9])([A-Z])", "$1-$2")
                .replace('_', '-')
                .toLowerCase(Locale.ROOT);

        return switch (value) {
            case "episode-type", "episode-types" -> "episode-types";
            case "event-type", "event-types" -> "event-types";
            case "attendance-status", "attendance-statuses" -> "attendance-statuses";
            case "closure-reason", "closure-reasons" -> "closure-reasons";
            case "program-population", "program-populations" -> "program-populations";
            case "program-modality", "program-modalities" -> "program-modalities";
            case "program-plan", "program-plans" -> "program-plans";
            case "document-type", "document-types" -> "document-types";
            case "region", "regions" -> "regions";
            case "city", "cities" -> "cities";
            case "semaphore-rule", "semaphore-rules" -> "semaphore-rules";
            default -> value;
        };
    }

    private void applyRequest(String catalog, Object entity, CatalogMaintainerRequest request, boolean partialUpdate) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body requerido");
        }

        if (entity instanceof SemaphoreRuleEntity semaphore) {
            if (!partialUpdate || request.getColorCode() != null || request.getCode() != null) {
                semaphore.setColorCode(firstNonBlank(request.getColorCode(), request.getCode(), partialUpdate ? semaphore.getColorCode() : null));
            }
            if (!partialUpdate || request.getName() != null) semaphore.setName(request.getName());
            if (!partialUpdate || request.getMinDays() != null) semaphore.setMinDays(request.getMinDays());
            if (!partialUpdate || request.getMaxDays() != null) semaphore.setMaxDays(request.getMaxDays());
            if (!partialUpdate || request.getActive() != null) semaphore.setActive(request.getActive() == null ? true : request.getActive());
            if (isBlank(semaphore.getColorCode()) || isBlank(semaphore.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "colorCode/code y name son obligatorios");
            }
            return;
        }

        if (entity instanceof BaseCatalogEntity base) {
            if (!partialUpdate || request.getCode() != null) base.setCode(request.getCode());
            if (!partialUpdate || request.getName() != null) base.setName(request.getName());
            if (!partialUpdate || request.getDescription() != null) base.setDescription(request.getDescription());
            if (!partialUpdate || request.getActive() != null) base.setActive(request.getActive() == null ? true : request.getActive());

            if (isBlank(base.getCode()) || isBlank(base.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code y name son obligatorios");
            }
        }

        if (entity instanceof CityEntity city) {
            if (!partialUpdate || request.getRegionId() != null) {
                if (request.getRegionId() == null) {
                    city.setRegion(null);
                } else {
                    RegionEntity region = regionRepository.findById(request.getRegionId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "regionId no existe"));
                    city.setRegion(region);
                }
            }
        }
    }

    private String firstNonBlank(String first, String second, String fallback) {
        if (!isBlank(first)) return first;
        if (!isBlank(second)) return second;
        return fallback;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private CatalogMaintainerDTO toDTO(Object entity) {
        if (entity instanceof SemaphoreRuleEntity s) {
            return CatalogMaintainerDTO.builder()
                    .id(s.getId())
                    .code(s.getColorCode())
                    .colorCode(s.getColorCode())
                    .name(s.getName())
                    .minDays(s.getMinDays())
                    .maxDays(s.getMaxDays())
                    .active(s.getActive())
                    .createdAt(s.getCreatedAt())
                    .updatedAt(s.getUpdatedAt())
                    .deletedAt(s.getDeletedAt())
                    .build();
        }

        if (entity instanceof CityEntity c) {
            RegionEntity region = c.getRegion();
            return CatalogMaintainerDTO.builder()
                    .id(c.getId())
                    .code(c.getCode())
                    .name(c.getName())
                    .description(c.getDescription())
                    .active(c.getActive())
                    .regionId(region != null ? region.getId() : null)
                    .regionCode(region != null ? region.getCode() : null)
                    .regionName(region != null ? region.getName() : null)
                    .createdAt(c.getCreatedAt())
                    .updatedAt(c.getUpdatedAt())
                    .deletedAt(c.getDeletedAt())
                    .build();
        }

        if (entity instanceof BaseCatalogEntity base) {
            return CatalogMaintainerDTO.builder()
                    .id(readId(entity))
                    .code(base.getCode())
                    .name(base.getName())
                    .description(base.getDescription())
                    .active(base.getActive())
                    .createdAt(base.getCreatedAt())
                    .updatedAt(base.getUpdatedAt())
                    .deletedAt(base.getDeletedAt())
                    .build();
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Entidad de catálogo no soportada");
    }

    private Integer readId(Object entity) {
        try {
            Method method = entity.getClass().getMethod("getId");
            Object value = method.invoke(entity);
            return value instanceof Integer ? (Integer) value : null;
        } catch (Exception e) {
            return null;
        }
    }
}
