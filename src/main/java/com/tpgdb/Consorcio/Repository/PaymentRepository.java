package com.tpgdb.Consorcio.Repository;

import com.tpgdb.Consorcio.Model.Payment;
import com.tpgdb.Consorcio.Model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPartnerId(Long partnerId);
    
    List<Payment> findByPeriod(LocalDate period);

    List<Payment> findByPartnerIdAndPeriod(Long partnerId, LocalDate period);

    List<Payment> findByPeriodBetween(LocalDate startPeriod, LocalDate endPeriod);

    boolean existsByPartnerAndPeriod(Partner partner, LocalDate period);
}