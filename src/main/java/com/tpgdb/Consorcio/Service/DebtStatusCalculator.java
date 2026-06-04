package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Model.Debt;
import com.tpgdb.Consorcio.Model.DebtStatus;
import com.tpgdb.Consorcio.Model.Expense;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@Component
public class DebtStatusCalculator {

    public LocalDate getDueDate(Debt debt) {
        if (debt == null) {
            return null;
        }
        if (debt.getDueDate() != null) {
            return debt.getDueDate();
        }

        Expense expense = debt.getExpense();
        if (expense == null || expense.getDate() == null) {
            return null;
        }

        return YearMonth.from(expense.getDate()).atEndOfMonth();
    }

    public LocalDate getMorosityStartDate(Debt debt) {
        LocalDate dueDate = getDueDate(debt);
        if (dueDate == null) {
            return null;
        }
        return dueDate.plusDays(1).plusMonths(1);
    }

    public DebtStatus getStatus(Debt debt) {
        return getStatus(debt, LocalDate.now());
    }

    public DebtStatus getStatus(Debt debt, LocalDate referenceDate) {
        if (debt == null) {
            return DebtStatus.PENDIENTE;
        }
        if (debt.isPaid()) {
            return DebtStatus.PAGADA;
        }

        LocalDate dueDate = getDueDate(debt);
        if (dueDate == null || !referenceDate.isAfter(dueDate)) {
            return DebtStatus.PENDIENTE;
        }

        LocalDate morosityStartDate = getMorosityStartDate(debt);
        if (morosityStartDate != null && !referenceDate.isBefore(morosityStartDate)) {
            return DebtStatus.EN_MORA;
        }

        return DebtStatus.VENCIDA;
    }

    public long getDaysOverdue(Debt debt) {
        LocalDate dueDate = getDueDate(debt);
        if (debt == null || debt.isPaid() || dueDate == null) {
            return 0;
        }
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, LocalDate.now()));
    }

    public long getDaysInMorosity(Debt debt) {
        if (getStatus(debt) != DebtStatus.EN_MORA) {
            return 0;
        }
        LocalDate morosityStartDate = getMorosityStartDate(debt);
        if (morosityStartDate == null) {
            return 0;
        }
        return Math.max(0, ChronoUnit.DAYS.between(morosityStartDate, LocalDate.now()));
    }
}
