package com.smartappointment.repository;

import com.smartappointment.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByActiveTrue();

    Optional<Location> findByIdAndActiveTrue(Long id);

    List<Location> findByCity(String city);

    List<Location> findByCityAndActiveTrue(String city);

    List<Location> findByNameContainingIgnoreCase(String name);

    boolean existsByNameAndCity(String name, String city);
}
