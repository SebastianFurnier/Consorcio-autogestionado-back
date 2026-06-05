package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.Dashboard.DashboardResponseDto;
import com.tpgdb.Consorcio.Dto.Expense.ExpenseResponseDto;
import com.tpgdb.Consorcio.Dto.partner.PartnerResponseDto;
import com.tpgdb.Consorcio.Dto.payment.PaymentResponseDto;
import com.tpgdb.Consorcio.Service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ExpenseService expenseService;
    private final PaymentService paymentService;
    private final PartnerService partnerService;
    private final BalanceService balanceService;

    public DashboardResponseDto getDashboardSummary(Long consorcioId, String period) {
        try {
                List<ExpenseResponseDto> gastos = (period != null)
                    ? expenseService.getExpensesByConsorcioAndPeriod(consorcioId, period)
                    : expenseService.getAllExpensesOfConsortium(consorcioId);

                List<PaymentResponseDto> pagos = (period != null)
                    ? paymentService.getPaymentsByConsorcioAndPeriod(consorcioId, period)
                    : paymentService.getAllPaymentsByConsorcio(consorcioId);

                List<PartnerResponseDto> socios = partnerService.getAllActivePartnersByConsorcio(consorcioId);

                // Use BalanceService to get canonical totals and per-partner balances
                var balance = balanceService.getBalanceOfConsorcio(consorcioId, period);

                float totalGastos = balance.getTotalExpenses();
                float totalPagos = balance.getTotalPayments();
                int gastosAprobados = (int) gastos.stream().filter(ExpenseResponseDto::isApproved).count();
                int gastosPendientes = (int) gastos.stream().filter(g -> !g.isApproved()).count();

                int totalSocios = socios.size();
                int sociosEnMora = balance.getCountPartnersInMorosity();

                DashboardResponseDto response = new DashboardResponseDto(
                    totalGastos,
                    totalPagos,
                    gastosAprobados,
                    gastosPendientes,
                    sociosEnMora,
                    balance.getCountPartnersWithOverdueDebt(),
                    totalSocios,
                    balance.getTotalOverdueDebt(),
                    balance.getTotalMoroseDebt(),
                    balance.getMorosityRate(),
                    balance.getTotalAccruedInterest(),
                    socios,
                    gastos,
                    pagos
                );

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error building dashboard summary", e);
        }
    }
}
