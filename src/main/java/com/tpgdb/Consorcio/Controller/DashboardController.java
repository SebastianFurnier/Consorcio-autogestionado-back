package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.Dashboard.DashboardResponseDto;
import com.tpgdb.Consorcio.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDto> getDashboard(@RequestParam(name = "consorcioId") Long consorcioId,
                                                              @RequestParam(name = "period", required = false) String period) {
        DashboardResponseDto dto = dashboardService.getDashboardSummary(consorcioId, period);
        return ResponseEntity.ok(dto);
    }
}
