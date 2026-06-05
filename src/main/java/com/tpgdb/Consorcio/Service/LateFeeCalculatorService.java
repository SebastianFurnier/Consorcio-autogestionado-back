package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioSettingsRequestDto;
import com.tpgdb.Consorcio.Model.Consorcio;
import com.tpgdb.Consorcio.Model.ConsorcioSettings;
import com.tpgdb.Consorcio.Model.Debt;
import com.tpgdb.Consorcio.Model.DebtStatus;
import com.tpgdb.Consorcio.Repository.ConsorcioRepository;
import com.tpgdb.Consorcio.Repository.ConsorcioSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Servicio de cálculo de intereses y recargos por mora.
 *
 * DECISIÓN DE DISEÑO: Cálculo Dinámico (no persistido).
 *
 * Se eligió calcular el interés dinámicamente en vez de persistirlo por las siguientes razones:
 *
 * 1. CONSISTENCIA: Un interés dinámico siempre refleja el estado actual (hoy). Un interés
 *    persistido requeriría un job nocturno que lo actualice, generando inconsistencias si el job
 *    falla o se retrasa.
 *
 * 2. SIMPLICIDAD: No se necesita una tabla extra ni un scheduler. El sistema es stateless
 *    en cuanto a los intereses: siempre se calculan fresh.
 *
 * 3. ESCALA ACTUAL: El número de deudas por consorcio es acotado (máximo cientos). El cálculo
 *    dinámico no presenta problema de performance.
 *
 * 4. CONFIGURABILIDAD: Si el admin cambia la tasa de mora, el cálculo se refleja inmediatamente
 *    en el balance sin necesidad de recalcular ni migrar datos históricos.
 *
 * FÓRMULA:
 *   interés_diario = (monthlyInterestRate / 100) / 30
 *   interés_acumulado = monto_deuda × interés_diario × días_en_mora
 *   penalidad_total = interés_acumulado + fixedPenalty (solo si entra por primera vez en mora)
 *
 * El recargo fijo solo se aplica cuando la deuda está en estado EN_MORA.
 */
@Service
@RequiredArgsConstructor
public class LateFeeCalculatorService {

    private final ConsorcioSettingsRepository settingsRepository;
    private final ConsorcioRepository consorcioRepository;
    private final DebtStatusCalculator debtStatusCalculator;

    /**
     * Obtiene los settings del consorcio, o crea defaults si no existen.
     */
    public ConsorcioSettings getOrDefaultSettings(Long consorcioId) {
        return settingsRepository.findByConsorcioId(consorcioId)
                .orElseGet(() -> {
                    // Retorna defaults sin persistir – el admin debe configurarlo explícitamente
                    ConsorcioSettings defaults = new ConsorcioSettings();
                    defaults.setConsorcioId(consorcioId);
                    defaults.setMonthlyInterestRate(3.0f);
                    defaults.setGracePeriodDays(0);
                    defaults.setFixedPenalty(0.0f);
                    return defaults;
                });
    }

    /**
     * Calcula el interés acumulado por mora para una deuda, usando la configuración del consorcio.
     *
     * @param debt       La deuda a evaluar
     * @param settings   Configuración del consorcio (tasa, gracia, recargo)
     * @return Interés acumulado en la moneda del sistema. 0 si la deuda no está en mora.
     */
    public float calculateAccruedInterest(Debt debt, ConsorcioSettings settings) {
        if (debt == null || debt.isPaid()) {
            return 0.0f;
        }

        int gracePeriodDays = settings != null ? settings.getGracePeriodDays() : 0;
        DebtStatus status = debtStatusCalculator.getStatus(debt, LocalDate.now(), gracePeriodDays);

        if (status != DebtStatus.EN_MORA) {
            return 0.0f;
        }

        long daysInMorosity = debtStatusCalculator.getDaysInMorosity(debt, gracePeriodDays);
        if (daysInMorosity <= 0) {
            return 0.0f;
        }

        float monthlyRate = settings != null ? settings.getMonthlyInterestRate() : 3.0f;
        float fixedPenalty = settings != null ? settings.getFixedPenalty() : 0.0f;

        // Interés proporcional por días (tasa mensual / 30 días)
        float dailyRate = (monthlyRate / 100.0f) / 30.0f;
        float proportionalInterest = debt.getAmount() * dailyRate * daysInMorosity;

        // Recargo fijo (se suma al interés proporcional cuando está en mora)
        float totalInterest = proportionalInterest + fixedPenalty;

        return Math.max(0.0f, totalInterest);
    }

    /**
     * Calcula el monto total a pagar para una deuda (capital + interés por mora).
     *
     * @param debt     La deuda
     * @param settings Configuración del consorcio
     * @return Monto total a pagar
     */
    public float calculateTotalOwed(Debt debt, ConsorcioSettings settings) {
        if (debt == null || debt.isPaid()) {
            return 0.0f;
        }
        return debt.getAmount() + calculateAccruedInterest(debt, settings);
    }

    /**
     * Guarda o actualiza la configuración de mora para un consorcio.
     */
    public ConsorcioSettings saveOrUpdateSettings(Long consorcioId, ConsorcioSettingsRequestDto requestDto) {
        Consorcio consorcio = consorcioRepository.findById(consorcioId)
                .orElseThrow(() -> new RuntimeException("Consorcio no encontrado con id: " + consorcioId));

        ConsorcioSettings settings = settingsRepository.findByConsorcioId(consorcioId)
                .orElseGet(() -> {
                    ConsorcioSettings s = new ConsorcioSettings();
                    s.setConsorcio(consorcio);
                    return s;
                });

        settings.setMonthlyInterestRate(requestDto.getMonthlyInterestRate());
        settings.setGracePeriodDays(requestDto.getGracePeriodDays());
        settings.setFixedPenalty(requestDto.getFixedPenalty());

        return settingsRepository.save(settings);
    }
}
