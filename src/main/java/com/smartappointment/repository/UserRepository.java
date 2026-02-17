package com.smartappointment.repository;

import com.smartappointment.model.entity.User;
import com.smartappointment.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByActiveTrue();

    List<User> findByActiveFalse();

    List<User> findByRole(Role role);

    List<User> findByRoleAndActiveTrue(Role role);

    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    long countByActiveTrue();

    long countByActiveFalse();

    long countByRole(Role role);
}
