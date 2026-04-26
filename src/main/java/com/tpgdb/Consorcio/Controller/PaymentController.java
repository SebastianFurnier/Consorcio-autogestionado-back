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
    public ResponseEntity<Map<String, List<PaymentResponseDto>>> getAllPayments() {
        List<PaymentResponseDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(Map.of("response", payments));
    }
}