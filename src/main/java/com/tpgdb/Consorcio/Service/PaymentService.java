package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.payment.PaymentRequestDto;
import com.tpgdb.Consorcio.Dto.payment.PaymentResponseDto;
import com.tpgdb.Consorcio.Exception.InvalidPartnerIDException;
import com.tpgdb.Consorcio.Model.*;
import com.tpgdb.Consorcio.Repository.DebtRepository;
import com.tpgdb.Consorcio.Repository.PaymentRepository;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.YearMonth;

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

        // CORRECCIÓN: El campo 'getExpenseId' del DTO trae en realidad el ID de la Deuda (Debt) desde el Front
        Debt debt = debtRepository.findById(paymentDto.getExpenseId())
                .orElseThrow(() -> new RuntimeException("No se encontró la deuda asociada con ID: " + paymentDto.getExpenseId()));

        debt.setPaid(true);
        debtRepository.save(debt);

        // Obtenemos el Gasto real asociado a esa Deuda para guardarlo en el Pago
        Expense expense = debt.getExpense();
        Consorcio consorcio = partner.getConsorcio();

        Payment payment = new Payment();
        payment.setPartner(partner);
        payment.setExpense(expense);
        payment.setPaymentDate(paymentDto.getPaymentDate());
        
        // Normalize period to the first day of the month (YearMonth) to ensure consistent periods
        if (paymentDto.getPeriod() != null) {
            payment.setPeriod(YearMonth.from(paymentDto.getPeriod()).atDay(1));
        } else {
            payment.setPeriod(YearMonth.from(paymentDto.getPaymentDate()).atDay(1));
        }
        
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
     */
    public List<PaymentResponseDto> getAllPaymentsByConsorcio(Long consorcioId) {
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
        LocalDate base = LocalDate.parse(period);
        YearMonth ym = YearMonth.from(base);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<Payment> payments = paymentRepository.findByConsorcioIdAndPeriodBetween(consorcioId, start, end);
        return payments.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    private PaymentResponseDto convertToResponseDto(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getPartner().getId(),
                payment.getExpense() != null ? payment.getExpense().getId() : null,
                payment.getPaymentDate(),
                payment.getPeriod(),
                payment.getPaymentMethod(),
                payment.getDescription(),
                payment.getAmount(),
                payment.getReceiptUrl()
        );
    }
}