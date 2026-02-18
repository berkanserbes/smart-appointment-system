package com.smartappointment.repository;

import com.smartappointment.model.entity.ServiceProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    Page<ServiceProvider> findByActiveTrue(Pageable pageable);

    Page<ServiceProvider> findByActiveFalse(Pageable pageable);

    Optional<ServiceProvider> findByIdAndActiveTrue(Long id);

    Page<ServiceProvider> findByCategoryId(Long categoryId, Pageable pageable);

    Page<ServiceProvider> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    Page<ServiceProvider> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
