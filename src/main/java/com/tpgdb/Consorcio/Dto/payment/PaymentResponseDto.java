package com.tpgdb.Consorcio.Dto.payment;

import com.tpgdb.Consorcio.Model.Payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentResponseDto {
    private Long id;
    private Long partnerId;
    private LocalDate paymentDate;
    private LocalDate period;
    private float amount;
    private PaymentMethod paymentMethod;
    private String description;
}