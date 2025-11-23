package com.guerrini.service;

import com.guerrini.dto.CreateUserRequest;
import com.guerrini.dto.UpdateUserRequest;
import com.guerrini.model.RolesType;
import com.guerrini.model.RolesTypeEntity;
import com.guerrini.model.User;
import com.guerrini.repository.RolesTypeRepository;
import com.guerrini.repository.UserRepository;
import com.guerrini.exception.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repository;
    private final RolesTypeRepository rolesTypeRepository;

    public UserService(UserRepository repository, RolesTypeRepository rolesTypeRepository) {
        this.repository = repository;
        this.rolesTypeRepository = rolesTypeRepository;
    }

    public List<User> listAll() {
        return repository.findAll();
    }

    public User getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    }

    @Transactional
    public User create(CreateUserRequest req) {
        if (repository.existsByEmail(req.getEmail())) {
            throw new DataIntegrityViolationException("Email already in use");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setFiscalCode(req.getCodiceFiscale());
        u.setFirstName(req.getNome());
        u.setLastName(req.getCognome());

        // Map roles from DTO (enum) to RolesTypeEntity
        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            Set<String> roleNames = req.getRoles().stream().map(RolesType::name).collect(Collectors.toSet());
            List<RolesTypeEntity> existing = rolesTypeRepository.findByNameIn(roleNames);
            Set<String> foundNames = existing.stream().map(RolesTypeEntity::getName).collect(Collectors.toSet());
            // create any missing role entities
            Set<RolesTypeEntity> roleEntities = new HashSet<>(existing);
            for (String rn : roleNames) {
                if (!foundNames.contains(rn)) {
                    RolesTypeEntity r = new RolesTypeEntity(rn);
                    roleEntities.add(rolesTypeRepository.save(r));
                }
            }
            u.setRoles(roleEntities);
        }

        return repository.save(u);
    }

    @Transactional
    public User update(Long id, UpdateUserRequest req) {
        User u = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        u.setUsername(req.getUsername());
        u.setFiscalCode(req.getCodiceFiscale());
        u.setFirstName(req.getNome());
        u.setLastName(req.getCognome());

        if (req.getRoles() != null) {
            Set<String> roleNames = req.getRoles().stream().map(RolesType::name).collect(Collectors.toSet());
            List<RolesTypeEntity> existing = rolesTypeRepository.findByNameIn(roleNames);
            Set<String> foundNames = existing.stream().map(RolesTypeEntity::getName).collect(Collectors.toSet());
            Set<RolesTypeEntity> roleEntities = new HashSet<>(existing);
            for (String rn : roleNames) {
                if (!foundNames.contains(rn)) {
                    RolesTypeEntity r = new RolesTypeEntity(rn);
                    roleEntities.add(rolesTypeRepository.save(r));
                }
            }
            u.setRoles(roleEntities);
        }

        return repository.save(u);
    }

    @Transactional
    public void delete(Long id) {
        User u = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        repository.delete(u);
    }
}
