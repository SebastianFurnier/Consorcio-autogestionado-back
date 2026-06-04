package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.Report.*;
import com.tpgdb.Consorcio.Service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly-summary/pdf")
    public ResponseEntity<byte[]> generateMonthlySummaryPdf(
            @RequestParam Long consorcioId,
            @RequestParam String period) {
        
        byte[] pdfBytes = reportService.generateMonthlySummaryPdf(consorcioId, period);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=resumen-" + period + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}