package com.guerrini.repository;

import com.guerrini.model.RolesTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolesTypeRepository extends JpaRepository<RolesTypeEntity, Long> {
    boolean existsByName(String name);
    Optional<RolesTypeEntity> findByName(String name);
    List<RolesTypeEntity> findByNameIn(Iterable<String> names);
}
