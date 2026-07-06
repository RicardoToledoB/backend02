package com.cosam.project01.controller;

import com.cosam.project01.dto.ServerTimeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/time")
public class TimeController {

    @Value("${app.time.zone:America/Punta_Arenas}")
    private String timeZone;

    @GetMapping("/server")
    public ResponseEntity<ServerTimeDTO> serverTime() {
        ZoneId zone = ZoneId.of(timeZone);
        Instant now = Clock.system(zone).instant();
        ZonedDateTime zonedDateTime = now.atZone(zone);

        return ResponseEntity.ok(ServerTimeDTO.builder()
                .epochMillis(now.toEpochMilli())
                .dateTime(zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .build());
    }
}
