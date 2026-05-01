package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.payment.PaymentRequestDto;
import com.tpgdb.Consorcio.Dto.payment.PaymentResponseDto;
import com.tpgdb.Consorcio.Model.Partner;
import com.tpgdb.Consorcio.Model.Payment;
import com.tpgdb.Consorcio.Repository.PaymentRepository;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PartnerRepository partnerRepository;

    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentDto) {
        Partner partner = partnerRepository.findById(paymentDto.getPartnerId())
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con ID: " + paymentDto.getPartnerId()));

        Payment payment = new Payment();
        payment.setPartner(partner);
        payment.setPaymentDate(paymentDto.getPaymentDate());
        payment.setPeriod(paymentDto.getPeriod());
        payment.setAmount(paymentDto.getAmount());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setDescription(paymentDto.getDescription());

        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponseDto(savedPayment);
    }

    /**
     * Obtiene todos los pagos filtrados por el ID del consorcio.
     * Este es el método que soluciona la duplicidad de datos en el Dashboard.
     */
    public List<PaymentResponseDto> getAllPaymentsByConsorcio(Long consorcioId) {
        // El repositorio debe tener definido findByPartner_Consorcio_Id
        List<Payment> payments = paymentRepository.findByPartner_Consorcio_Id(consorcioId);
        return payments.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    public List<PaymentResponseDto> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    private PaymentResponseDto convertToResponseDto(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getPartner().getId(),
                payment.getPaymentDate(),
                payment.getPeriod(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getDescription());
    }
}