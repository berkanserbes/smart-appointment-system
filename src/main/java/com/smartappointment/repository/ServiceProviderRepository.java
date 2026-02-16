package com.smartappointment.repository;

import com.smartappointment.model.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    List<ServiceProvider> findByActiveTrue();

    Optional<ServiceProvider> findByIdAndActiveTrue(Long id);

    List<ServiceProvider> findByCategoryId(Long categoryId);

    List<ServiceProvider> findByCategoryIdAndActiveTrue(Long categoryId);

    List<ServiceProvider> findByNameContainingIgnoreCase(String name);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
