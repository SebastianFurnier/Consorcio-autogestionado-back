package com.tpgdb.Consorcio.Dto.Dashboard;

import com.tpgdb.Consorcio.Dto.Expense.ExpenseResponseDto;
import com.tpgdb.Consorcio.Dto.payment.PaymentResponseDto;
import com.tpgdb.Consorcio.Dto.partner.PartnerResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DashboardResponseDto {
    private float totalGastos;
    private float totalPagos;
    private int gastosAprobados;
    private int gastosPendientes;
    private int sociosEnMora;
    private int sociosConDeudaVencida;
    private int totalSocios;
    private float deudaTotalVencida;
    private float deudaTotalEnMora;
    private float porcentajeSociosMorosos;
    private List<PartnerResponseDto> socios;
    private List<ExpenseResponseDto> gastos;
    private List<PaymentResponseDto> pagos;
}
