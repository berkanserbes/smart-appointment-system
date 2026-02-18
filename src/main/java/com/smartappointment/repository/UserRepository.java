package com.smartappointment.repository;

import com.smartappointment.model.entity.User;
import com.smartappointment.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByActiveTrue(Pageable pageable);

    Page<User> findByActiveFalse(Pageable pageable);

    Page<User> findByRole(Role role, Pageable pageable);

    Page<User> findByRoleAndActiveTrue(Role role, Pageable pageable);

    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName, Pageable pageable);

    long countByActiveTrue();

    long countByActiveFalse();

    long countByRole(Role role);
}
