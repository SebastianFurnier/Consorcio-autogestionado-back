package com.tpgdb.Consorcio.Repository;

import com.tpgdb.Consorcio.Model.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {

    // === ESTA ES LA LÍNEA QUE AGREGAMOS PARA SOLUCIONAR EL ERROR DE COMPILACIÓN ===
    List<Debt> findByConsorcio_idAndPartner_id(Long consorcioId, Long partnerId);

    List<Debt> findByConsorcio_id(Long consorcioId);
    List<Debt> findByPaidIsFalseAndConsorcio_idAndPartner_id(Long consorcioId, Long partnerId);
    List<Debt> findByPaidIsTrueAndConsorcio_idAndPartner_id(Long consorcioId, Long partnerId);

    // Period-aware queries (expense.date is used as the source of the period for debts)
    List<Debt> findByConsorcio_idAndExpense_DateBetween(Long consorcioId, LocalDate start, LocalDate end);
    List<Debt> findByConsorcio_idAndPartner_idAndExpense_DateBetween(Long consorcioId, Long partnerId, LocalDate start, LocalDate end);
    List<Debt> findByPaidIsFalseAndConsorcio_idAndPartner_idAndExpense_DateBetween(Long consorcioId, Long partnerId, LocalDate start, LocalDate end);
    List<Debt> findByPaidIsTrueAndConsorcio_idAndPartner_idAndExpense_DateBetween(Long consorcioId, Long partnerId, LocalDate start, LocalDate end);

    @Query("""
        SELECT COUNT(DISTINCT d.partner.id)
        FROM Debt d
        WHERE d.paid = false AND d.consorcio.id = :consorcioId
    """)
    Integer countDistinctPartnersWithUnpaidDebts(Long consorcioId);

    @Query("""
        SELECT COUNT(DISTINCT d.partner.id)
        FROM Debt d
        WHERE d.paid = false 
          AND d.consorcio.id = :consorcioId 
          AND d.expense.date BETWEEN :start AND :end
    """)
    Integer countDistinctPartnersWithUnpaidDebtsBetween(Long consorcioId, LocalDate start, LocalDate end);
}