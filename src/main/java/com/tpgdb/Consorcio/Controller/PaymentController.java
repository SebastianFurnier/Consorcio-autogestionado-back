package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.partner.PartnerResponseDto;
import com.tpgdb.Consorcio.Dto.payment.PaymentResponseDto;
import com.tpgdb.Consorcio.Dto.payment.PaymentRequestDto;
import com.tpgdb.Consorcio.Service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/save")
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentRequestDto paymentDto) {
        PaymentResponseDto responseDto = paymentService.createPayment(paymentDto);
        return ResponseEntity.ok(Map.of("response", responseDto));
    }

    /**
     * Listar todos los pagos (temporal, voy a agregar filtros)
     */
    @GetMapping("/all")
    // Agregamos @RequestParam para capturar el ID que viene en la URL
    public ResponseEntity<Map<String, List<PaymentResponseDto>>> getAllPayments(
            @RequestParam(name = "consorcioId") Long consorcioId) {
        // Pasamos el ID al service
        List<PaymentResponseDto> payments = paymentService.getAllPaymentsByConsorcio(consorcioId);
        return ResponseEntity.ok(Map.of("response", payments));
    }

    @GetMapping("/period")
    public ResponseEntity<Map<String, List<PaymentResponseDto>>> getPaymentsByPeriod(
            @RequestParam(name = "consorcioId") Long consorcioId,
            @RequestParam(name = "period") String period) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByConsorcioAndPeriod(consorcioId, period);
        return ResponseEntity.ok(Map.of("response", payments));
    }
}