package com.tpgdb.Consorcio.Repository;

import com.tpgdb.Consorcio.Model.Partner;
import com.tpgdb.Consorcio.Model.Consorcio;
import com.tpgdb.Consorcio.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    List<Partner> findAllByActiveIsTrue();
    
    List<Partner> findByUserIdAndActiveIsTrue(Long userId);
    List<Partner> findByConsorcioIdAndActiveIsTrue(Long consortioId);
    
    Optional<Partner> findByUserAndConsorcio(User user, Consorcio consorcio);
    boolean existsByUserAndConsorcio(User user, Consorcio consorcio);
    
    boolean existsPartnerByApartmentAndActiveIsTrue(String apartment);
}

