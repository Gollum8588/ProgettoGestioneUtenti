package org.example.controller;

import org.example.dto.CreateUserRequest;
import org.example.dto.UpdateUserRequest;
import org.example.exception.UserNotFoundException;
import org.example.model.RolesType;
import org.example.model.User;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController controller;

    private CreateUserRequest sampleCreateUserRequest;
    private User mockUser;
    private Long userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        when(userService.listAll()).thenReturn(users);

        List<User> result = controller.listAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("test@example.com", result.get(0).getEmail());
    }

    @Test
    void testGetUserById() {
        when(userService.getById(userId)).thenReturn(mockUser);

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

        when(userService.create(any(CreateUserRequest.class))).thenReturn(createdUser);

        // Prepare a mock servlet request so ServletUriComponentsBuilder can build the Location header
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setMethod("POST");
        servletRequest.setRequestURI("/api/users");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

        var response = controller.create(request);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("newuser", response.getBody().getUsername());
        assertEquals("newuser@example.com", response.getBody().getEmail());

        // Clear the request attributes after the test to avoid side-effects
        RequestContextHolder.resetRequestAttributes();
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

        when(userService.update(eq(userId), any(UpdateUserRequest.class))).thenReturn(updatedUser);

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
        doNothing().when(userService).delete(userId);

        var response = controller.delete(userId);
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());

        when(userService.getById(userId)).thenThrow(new UserNotFoundException("User not found with id " + userId));

        assertThrows(UserNotFoundException.class, () -> controller.getById(userId));
    }
}
