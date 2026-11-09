package com.hogar360.statistics.infrastructure.adapter.in.web;

import com.hogar360.statistics.domain.model.StatisticsPeriod;
import com.hogar360.statistics.domain.port.in.GetStatisticsUseCase;
import com.hogar360.statistics.infrastructure.adapter.in.web.dto.StatisticsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Estadísticas del hogar (solo lectura). Protegido por el JwtAuthenticationFilter:
 * authentication.getName() es el email del JWT.
 */
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final GetStatisticsUseCase getStatisticsUseCase;

    public StatisticsController(GetStatisticsUseCase getStatisticsUseCase) {
        this.getStatisticsUseCase = getStatisticsUseCase;
    }

    @GetMapping
    public ResponseEntity<StatisticsResponse> get(
            @RequestParam(defaultValue = "MONTH") StatisticsPeriod period,
            Authentication authentication) {
        return ResponseEntity.ok(StatisticsResponse.from(
                getStatisticsUseCase.get(authentication.getName(), period)));
    }
}