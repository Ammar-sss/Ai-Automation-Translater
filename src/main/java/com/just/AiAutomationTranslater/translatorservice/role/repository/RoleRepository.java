package com.just.AiAutomationTranslater.translatorservice.role.repository;

import com.just.AiAutomationTranslater.translatorservice.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);
}
