package org.example.service;

import org.example.dto.CreateUserRequest;
import org.example.dto.UpdateUserRequest;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.exception.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
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
//        u.setRoles(req.getRoles());
        return repository.save(u);
    }

    @Transactional
    public User update(Long id, UpdateUserRequest req) {
        User u = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        u.setUsername(req.getUsername());
        u.setFiscalCode(req.getCodiceFiscale());
        u.setFirstName(req.getNome());
        u.setLastName(req.getCognome());
//        u.setRoles(req.getRoles());
        return repository.save(u);
    }

    @Transactional
    public void delete(Long id) {
        User u = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        repository.delete(u);
    }
}
