package com.smartappointment.repository;

import com.smartappointment.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Category> findByActiveTrue();

    List<Category> findByActiveFalse();

    Optional<Category> findByIdAndActiveTrue(Long id);

    boolean existsByNameIgnoreCaseAndActiveTrue(String name);

    List<Category> findByNameContainingIgnoreCase(String keyword);

    List<Category> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);

    long countByActiveTrue();

    long countByActiveFalse();

    List<Category> findByActiveTrueOrderByNameAsc();
}
