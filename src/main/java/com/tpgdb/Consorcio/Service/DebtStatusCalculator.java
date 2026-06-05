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

    /**
     * Obtiene la fecha de vencimiento de una deuda.
     * Por defecto: último día del mes del gasto asociado.
     */
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

    /**
     * Calcula la fecha desde la cual se considera EN_MORA.
     * Se considera EN_MORA cuando han pasado al menos un mes completo + gracePeriodDays
     * sin que se haya pagado, contados desde la fecha de vencimiento.
     *
     * @param debt           La deuda a evaluar
     * @param gracePeriodDays Días de gracia configurados para el consorcio (default 0)
     */
    public LocalDate getMorosityStartDate(Debt debt, int gracePeriodDays) {
        LocalDate dueDate = getDueDate(debt);
        if (dueDate == null) {
            return null;
        }
        // La deuda vence al final del mes. Pasa a VENCIDA el día siguiente.
        // Pasa a EN_MORA después de un mes completo de gracia + los días de gracia configurados.
        return dueDate.plusMonths(1).plusDays(1 + gracePeriodDays);
    }

    /**
     * Versión sin gracia (backward compatible, usa 0 días de gracia).
     */
    public LocalDate getMorosityStartDate(Debt debt) {
        return getMorosityStartDate(debt, 0);
    }

    /**
     * Determina el estado actual de la deuda con days of grace configurable.
     */
    public DebtStatus getStatus(Debt debt, LocalDate referenceDate, int gracePeriodDays) {
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

        LocalDate morosityStartDate = getMorosityStartDate(debt, gracePeriodDays);
        if (morosityStartDate != null && !referenceDate.isBefore(morosityStartDate)) {
            return DebtStatus.EN_MORA;
        }

        return DebtStatus.VENCIDA;
    }

    /**
     * Backward-compatible: usa 0 días de gracia.
     */
    public DebtStatus getStatus(Debt debt, LocalDate referenceDate) {
        return getStatus(debt, referenceDate, 0);
    }

    public DebtStatus getStatus(Debt debt) {
        return getStatus(debt, LocalDate.now(), 0);
    }

    /**
     * Días que lleva la deuda vencida (desde dueDate hasta hoy, 0 si no vencida).
     */
    public long getDaysOverdue(Debt debt) {
        LocalDate dueDate = getDueDate(debt);
        if (debt == null || debt.isPaid() || dueDate == null) {
            return 0;
        }
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, LocalDate.now()));
    }

    /**
     * Días en mora (desde morosityStartDate hasta hoy, usando gracePeriodDays).
     */
    public long getDaysInMorosity(Debt debt, int gracePeriodDays) {
        if (getStatus(debt, LocalDate.now(), gracePeriodDays) != DebtStatus.EN_MORA) {
            return 0;
        }
        LocalDate morosityStartDate = getMorosityStartDate(debt, gracePeriodDays);
        if (morosityStartDate == null) {
            return 0;
        }
        return Math.max(0, ChronoUnit.DAYS.between(morosityStartDate, LocalDate.now()));
    }

    /**
     * Backward-compatible: usa 0 días de gracia.
     */
    public long getDaysInMorosity(Debt debt) {
        return getDaysInMorosity(debt, 0);
    }
}
