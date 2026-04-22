package com.tpgdb.Consorcio.Repository;

import com.tpgdb.Consorcio.Model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    Optional<Partner> findByIdAndActiveIsTrue(Long id);
    List<Partner> findAllByActiveIsTrue();
    boolean existsPartnerByApartmentAndActiveIsTrue(String apartment);
}
