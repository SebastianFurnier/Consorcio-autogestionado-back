package com.tpgdb.Consorcio.Repository;

import com.tpgdb.Consorcio.Model.Consorcio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsorcioRepository extends JpaRepository<Consorcio, Long> {
    Optional<Consorcio> findByCodigoInvitacion(String codigoInvitacion);
    List<Consorcio> findAllByActiveIsTrue();
    boolean existsByCodigoInvitacion(String codigoInvitacion);
}
