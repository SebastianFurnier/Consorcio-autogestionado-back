package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.payment.PaymentRequestDto;
import com.tpgdb.Consorcio.Dto.payment.PaymentResponseDto;
import com.tpgdb.Consorcio.Exception.InvalidPartnerIDException;
import com.tpgdb.Consorcio.Model.*;
import com.tpgdb.Consorcio.Repository.DebtRepository;
import com.tpgdb.Consorcio.Repository.PaymentRepository;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import jakarta.transaction.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PartnerRepository partnerRepository;
    private final DebtRepository debtRepository;

    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentDto, String url) {
        Partner partner = partnerRepository.findById(paymentDto.getPartnerId())
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con ID: " + paymentDto.getPartnerId()));

        Debt debt = debtRepository.findById(paymentDto.getExpenseId()).orElseThrow( () ->
                new InvalidPartnerIDException("No se encontro el pago asociado")
        );

        debt.setPaid(true);
        debtRepository.save(debt);

        Expense expense = debt.getExpense();
        Consorcio consorcio = partner.getConsorcio();

        Payment payment = new Payment();
        payment.setPartner(partner);
        payment.setExpense(expense);
        payment.setPaymentDate(paymentDto.getPaymentDate());
        payment.setPeriod(paymentDto.getPeriod());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setDescription(paymentDto.getDescription());
        payment.setAmount(debt.getAmount());
        payment.setReceiptUrl(url);
        payment.setConsorcioId(consorcio.getId());

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

    public List<PaymentResponseDto> getPaymentsByConsorcioAndPeriod(Long consorcioId, String period) {
        List<Payment> payments = paymentRepository.findByConsorcioIdAndPeriodGreaterThanEqual(consorcioId, LocalDate.parse(period));
        return payments.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    private PaymentResponseDto convertToResponseDto(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getPartner().getId(),
                payment.getExpense().getId(),
                payment.getPaymentDate(),
                payment.getPeriod(),
                payment.getPaymentMethod(),
                payment.getDescription(),
                payment.getAmount(),
                payment.getReceiptUrl()
                );
    }
}