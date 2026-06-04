package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.payment.PaymentResponseDto;
import com.tpgdb.Consorcio.Dto.payment.PaymentRequestDto;
import com.tpgdb.Consorcio.Service.DebtService;
import com.tpgdb.Consorcio.Service.PaymentService;
import com.tpgdb.Consorcio.Service.UploadImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final DebtService debtService;
    private final UploadImageService imageService;
    private final com.tpgdb.Consorcio.Service.DashboardService dashboardService;

    @PostMapping(
            value = "/save",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> createPayment(@Valid @RequestPart("paymentDto") PaymentRequestDto paymentDto,
                                           @RequestPart("file") MultipartFile file) { 
        try {

            String filename = imageService.uploadFile(file);
            
            PaymentResponseDto responseDto = paymentService.createPayment(paymentDto, filename);

            return ResponseEntity.ok(Map.of("response", responseDto));
            
        } catch (Exception e) {
            e.printStackTrace(); 
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error interno en el servidor",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Listar todos los pagos filtrados por consorcio
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, List<PaymentResponseDto>>> getAllPayments(
            @RequestParam(name = "consorcioId") Long consorcioId) {
        List<PaymentResponseDto> payments = paymentService.getAllPaymentsByConsorcio(consorcioId);
        return ResponseEntity.ok(Map.of("response", payments));
    }

    /**
     * Listar pagos por consorcio y período
     */
    @GetMapping("/period")
    public ResponseEntity<Map<String, List<PaymentResponseDto>>> getPaymentsByPeriod(
            @RequestParam(name = "consorcioId") Long consorcioId,
            @RequestParam(name = "period") String period) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByConsorcioAndPeriod(consorcioId, period);
        return ResponseEntity.ok(Map.of("response", payments));
    }

    /**
     * Obtener cantidad de socios al día
     */
    @GetMapping("socios-al-dia")
    public ResponseEntity<Map<String, Integer>> getSociosAlDia(
            @RequestParam(name = "consorcioId") Long consorcioId,
            @RequestParam(name = "period", required = false) String period) {
        var dashboard = dashboardService.getDashboardSummary(consorcioId, period);
        int amountUpToDate = dashboard.getTotalSocios() - dashboard.getSociosConDeudaVencida();
        return ResponseEntity.ok(Map.of("amount", amountUpToDate));
    }
}
