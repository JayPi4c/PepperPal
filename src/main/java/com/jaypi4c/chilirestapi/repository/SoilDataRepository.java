package com.jaypi4c.chilirestapi.repository;

import com.jaypi4c.chilirestapi.model.SoilData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface SoilDataRepository extends JpaRepository<SoilData, UUID> {

    @Query(value = "SELECT * FROM soil_data WHERE created BETWEEN ?1 AND ?2", nativeQuery = true)
    Page<SoilData> findByDateBetween(LocalDateTime begin, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT * FROM soil_data ORDER BY created DESC LIMIT 1", nativeQuery = true)
    Optional<SoilData> findLatest(boolean updated);

}
