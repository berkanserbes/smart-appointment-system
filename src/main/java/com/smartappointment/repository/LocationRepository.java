package com.smartappointment.repository;

import com.smartappointment.model.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Page<Location> findByActiveTrue(Pageable pageable);

    Page<Location> findByActiveFalse(Pageable pageable);

    Optional<Location> findByIdAndActiveTrue(Long id);

    Page<Location> findByCity(String city, Pageable pageable);

    Page<Location> findByCityAndActiveTrue(String city, Pageable pageable);

    Page<Location> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByNameAndCity(String name, String city);
}
