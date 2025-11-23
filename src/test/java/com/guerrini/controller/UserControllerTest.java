package com.guerrini.controller;

import com.guerrini.dto.CreateUserRequest;
import com.guerrini.dto.UpdateUserRequest;
import com.guerrini.exception.UserNotFoundException;
import com.guerrini.model.RolesType;
import com.guerrini.model.RolesTypeEntity;
import com.guerrini.model.User;
import com.guerrini.repository.RolesTypeRepository;
import com.guerrini.repository.UserRepository;
import com.guerrini.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserRepository userRepository;
    private RolesTypeRepository rolesTypeRepository;
    private UserService userService;
    private UserController controller;

    private CreateUserRequest sampleCreateUserRequest;
    private User mockUser;
    private Long userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRepository = mock(UserRepository.class);
        rolesTypeRepository = mock(RolesTypeRepository.class);
        userService = new UserService(userRepository, rolesTypeRepository);
        controller = new UserController(userService);

        sampleCreateUserRequest = new CreateUserRequest();
        sampleCreateUserRequest.setUsername("testuser");
        sampleCreateUserRequest.setEmail("test@example.com");
        sampleCreateUserRequest.setCodiceFiscale("ABCDEF12G34HI567J");
        sampleCreateUserRequest.setNome("Test");
        sampleCreateUserRequest.setCognome("User");
        sampleCreateUserRequest.setRoles(Set.of(RolesType.OPERATOR));

        userId = 1L;
        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setFiscalCode("ABCDEF12G34HI567J");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
    }

    @Test
    void testListAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(mockUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = controller.listAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("test@example.com", result.get(0).getEmail());
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = controller.getById(userId);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
    }

    @Test
    void testCreateUserWithRoles() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setCodiceFiscale("XYZABC12D34EF567G");
        request.setNome("New");
        request.setCognome("User");
        request.setRoles(Set.of(RolesType.DEVELOPER, RolesType.REPORTER));

        User createdUser = new User();
        createdUser.setId(2L);
        createdUser.setUsername("newuser");
        createdUser.setEmail("newuser@example.com");
        createdUser.setFiscalCode("XYZABC12D34EF567G");
        createdUser.setFirstName("New");
        createdUser.setLastName("User");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(rolesTypeRepository.findByNameIn(anyCollection())).thenReturn(Collections.emptyList());
        when(rolesTypeRepository.save(any(RolesTypeEntity.class))).thenAnswer(invocation -> {
            RolesTypeEntity r = invocation.getArgument(0);
            r.setId(new Random().nextLong() & Long.MAX_VALUE);
            return r;
        });
        when(userRepository.save(any(User.class))).thenReturn(createdUser);

        var response = controller.create(request);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("newuser", response.getBody().getUsername());
        assertEquals("newuser@example.com", response.getBody().getEmail());
    }

    @Test
    void testUpdateUserWithRoles() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setCodiceFiscale("UPDATED12CD34EF567");
        updateRequest.setNome("Updated");
        updateRequest.setCognome("Name");
        updateRequest.setRoles(Set.of(RolesType.OWNER, RolesType.MAINTAINER));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setFiscalCode("UPDATED12CD34EF567");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(rolesTypeRepository.findByNameIn(anyCollection())).thenReturn(Collections.emptyList());
        when(rolesTypeRepository.save(any(RolesTypeEntity.class))).thenAnswer(invocation -> {
            RolesTypeEntity r = invocation.getArgument(0);
            r.setId(new Random().nextLong() & Long.MAX_VALUE);
            return r;
        });
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = controller.update(userId, updateRequest);

        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("UPDATED12CD34EF567", result.getFiscalCode());
        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getLastName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser)).thenReturn(Optional.empty());
        doNothing().when(userRepository).delete(any(User.class));

        var response = controller.delete(userId);
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());

        assertThrows(UserNotFoundException.class, () -> controller.getById(userId));
    }
}
