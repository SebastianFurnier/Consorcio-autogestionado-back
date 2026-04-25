package com.tpgdb.Consorcio.Repository;

import com.tpgdb.Consorcio.Model.Consorcio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsorcioRepository extends JpaRepository<Consorcio, Long> {
    Optional<Consorcio> findByIdAndActiveIsTrue(Long id);
    Optional<Consorcio> findByCodigoInvitacion(String codigoInvitacion);
    boolean existsByCodigoInvitacion(String codigoInvitacion);
}
