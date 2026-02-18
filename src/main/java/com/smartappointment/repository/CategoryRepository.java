package com.smartappointment.repository;

import com.smartappointment.model.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    Page<Category> findByActiveTrue(Pageable pageable);

    Page<Category> findByActiveFalse(Pageable pageable);

    Optional<Category> findByIdAndActiveTrue(Long id);

    boolean existsByNameIgnoreCaseAndActiveTrue(String name);

    Page<Category> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Category> findByNameContainingIgnoreCaseAndActiveTrue(String keyword, Pageable pageable);

    long countByActiveTrue();

    long countByActiveFalse();
}
