package com.tpgdb.Consorcio.Repository;

import com.tpgdb.Consorcio.Model.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByConsorcio_id(Long consorcioId);
    List<Debt> findByPaidIsFalseAndConsorcio_idAndPartner_id(Long consorcioId, Long partnerId);
    List<Debt> findByPaidIsTrueAndConsorcio_idAndPartner_id(Long consorcioId, Long partnerId);

    @Query("""
        SELECT COUNT(DISTINCT d.partner.id)
        FROM Debt d
        WHERE d.paid = false AND d.consorcio.id = :consorcioId
    """)
    Integer countDistinctPartnersWithUnpaidDebts(Long consorcioId);
}
