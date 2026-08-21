package com.cosam.project01.service.impl;

import com.cosam.project01.demand.entity.CityEntity;
import com.cosam.project01.demand.repository.CityRepository;
import com.cosam.project01.entity.CommuneEntity;
import com.cosam.project01.repository.CommuneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mantiene homologada la tabla historica communes con el catalogo oficial cities.
 *
 * Contexto:
 * - /api/v1/demand/maintainers/cities es el catalogo oficial que utiliza el frontend.
 * - /api/v1/postulants mantiene una relacion historica con communes mediante commune_id.
 *
 * Mientras no se migre fisicamente postulants.commune_id a city_id, esta sincronizacion
 * evita errores al crear o actualizar postulantes con IDs validos de cities que no existen
 * aun en communes, por ejemplo Porvenir id=3.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommuneCitySynchronizationService {

    private final CityRepository cityRepository;
    private final CommuneRepository communeRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void syncCommunesFromCitiesOnStartup() {
        int createdOrUpdated = 0;

        for (CityEntity city : cityRepository.findAll()) {
            if (!isActiveCity(city)) {
                continue;
            }

            CommuneEntity commune = communeRepository.findAnyById(city.getId())
                    .orElseGet(() -> CommuneEntity.builder()
                            .id(city.getId())
                            .build());

            commune.setName(city.getName());
            commune.setDeletedAt(null);
            communeRepository.save(commune);
            createdOrUpdated++;
        }

        log.info("Sincronizacion communes/cities finalizada. Registros activos sincronizados: {}", createdOrUpdated);
    }

    private boolean isActiveCity(CityEntity city) {
        return city != null
                && city.getDeletedAt() == null
                && !Boolean.FALSE.equals(city.getActive());
    }
}
