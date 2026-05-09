package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.payment.PaymentResponseDto;
import com.tpgdb.Consorcio.Dto.payment.PaymentRequestDto;
import com.tpgdb.Consorcio.Service.DebtService;
import com.tpgdb.Consorcio.Service.ImageService;
import com.tpgdb.Consorcio.Service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
    public class PaymentController {
        private final PaymentService paymentService;
        private final DebtService debtService;
        private final ImageService imageService;

        @PostMapping(
                value = "/save",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE
        )
        public ResponseEntity<?> createPayment(@Valid @RequestPart("paymentDto") PaymentRequestDto paymentDto,
                                               @RequestParam("file") MultipartFile file) throws IOException {
            String imageName = file.getOriginalFilename();
            UUID hash = UUID.randomUUID();

            String filename = (hash.toString()) + imageName;
            PaymentResponseDto responseDto = paymentService.createPayment(paymentDto, filename);
            imageService.uploadImage(filename, file.getBytes());
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

    @GetMapping("socios-al-dia")
    public ResponseEntity<Map<String, Integer>> getSociosAlDia(@RequestParam(name = "consorcioId") Long consorcioId) {
        Integer amountDebtors = debtService.getSociosAlDia(consorcioId);

        return ResponseEntity.ok(Map.of("amount", amountDebtors));
    }
}