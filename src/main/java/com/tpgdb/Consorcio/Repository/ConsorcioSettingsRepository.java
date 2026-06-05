package com.tpgdb.Consorcio.Repository;

import com.tpgdb.Consorcio.Model.ConsorcioSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsorcioSettingsRepository extends JpaRepository<ConsorcioSettings, Long> {
    Optional<ConsorcioSettings> findByConsorcioId(Long consorcioId);
}
