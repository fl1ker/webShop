package com.example.onlineShop;

import com.example.onlineShop.models.User;
import com.example.onlineShop.models.enums.Role;
import com.example.onlineShop.repositories.UserRepository;
import com.example.onlineShop.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Principal principal;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setActive(true);
        testUser.setRoles(new HashSet<>());
    }

    @Test
    void createUser_WhenUserDoesNotExist_ShouldCreateUserSuccessfully() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        boolean result = userService.createUser(testUser);

        // Then
        assertTrue(result);
        assertTrue(testUser.isActive());
        assertEquals("encodedPassword", testUser.getPassword());
        assertTrue(testUser.getRoles().contains(Role.ROLE_USER));
        verify(userRepository).save(testUser);
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void createUser_WhenUserAlreadyExists_ShouldReturnFalse() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

        // When
        boolean result = userService.createUser(testUser);

        // Then
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void list_ShouldReturnAllUsers() {
        // Given
        List<User> expectedUsers = Arrays.asList(testUser, new User());
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // When
        List<User> result = userService.list();

        // Then
        assertEquals(expectedUsers, result);
        verify(userRepository).findAll();
    }

    @Test
    void banUser_WhenUserIsActive_ShouldDeactivateUser() {
        // Given
        testUser.setActive(true);
        when(userRepository.findById(1L)).thenReturn(testUser);

        // When
        userService.banUser(1L);

        // Then
        assertFalse(testUser.isActive());
        verify(userRepository).save(testUser);
    }

    @Test
    void banUser_WhenUserIsInactive_ShouldActivateUser() {
        // Given
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(testUser);

        // When
        userService.banUser(1L);

        // Then
        assertTrue(testUser.isActive());
        verify(userRepository).save(testUser);
    }

    @Test
    void banUser_WhenUserNotFound_ShouldHandleGracefully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(null);

        // When & Then
        assertDoesNotThrow(() -> userService.banUser(1L));
        verify(userRepository).save(null); // Current implementation saves null
    }

    @Test
    void changeUserRoles_ShouldUpdateUserRoles() {
        // Given
        testUser.getRoles().add(Role.ROLE_USER);
        Map<String, String> form = new HashMap<>();
        form.put("ROLE_ADMIN", "on");
        form.put("ROLE_USER", "on");
        form.put("INVALID_ROLE", "on"); // Should be ignored

        // When
        userService.changeUserRoles(testUser, form);

        // Then
        assertEquals(2, testUser.getRoles().size());
        assertTrue(testUser.getRoles().contains(Role.ROLE_ADMIN));
        assertTrue(testUser.getRoles().contains(Role.ROLE_USER));
        verify(userRepository).save(testUser);
    }

    @Test
    void changeUserRoles_WithEmptyForm_ShouldClearAllRoles() {
        // Given
        testUser.getRoles().add(Role.ROLE_USER);
        testUser.getRoles().add(Role.ROLE_ADMIN);
        Map<String, String> form = new HashMap<>();

        // When
        userService.changeUserRoles(testUser, form);

        // Then
        assertTrue(testUser.getRoles().isEmpty());
        verify(userRepository).save(testUser);
    }

    @Test
    void changeUserRoles_WithInvalidRoles_ShouldIgnoreInvalidRoles() {
        // Given
        Map<String, String> form = new HashMap<>();
        form.put("INVALID_ROLE", "on");
        form.put("ANOTHER_INVALID", "on");

        // When
        userService.changeUserRoles(testUser, form);

        // Then
        assertTrue(testUser.getRoles().isEmpty());
        verify(userRepository).save(testUser);
    }

    @Test
    void getUserByPrincipal_WhenPrincipalIsNull_ShouldReturnNewUser() {
        // When
        User result = userService.getUserByPrincipal(null);

        // Then
        assertNotNull(result);
        assertNull(result.getEmail());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void getUserByPrincipal_WhenPrincipalExists_ShouldReturnUser() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // When
        User result = userService.getUserByPrincipal(principal);

        // Then
        assertEquals(testUser, result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getUserById_ShouldReturnUserById() {
        // Given
        when(userRepository.findById(1L)).thenReturn(testUser);

        // When
        User result = userService.getUserById(1L);

        // Then
        assertEquals(testUser, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturnNull() {
        // Given
        when(userRepository.findById(999L)).thenReturn(null);

        // When
        User result = userService.getUserById(999L);

        // Then
        assertNull(result);
        verify(userRepository).findById(999L);
    }

    // Integration-style tests for role management
    @Test
    void changeUserRoles_WithAllValidRoles_ShouldSetAllRoles() {
        // Given
        Map<String, String> form = new HashMap<>();
        for (Role role : Role.values()) {
            form.put(role.name(), "on");
        }

        // When
        userService.changeUserRoles(testUser, form);

        // Then
        assertEquals(Role.values().length, testUser.getRoles().size());
        for (Role role : Role.values()) {
            assertTrue(testUser.getRoles().contains(role));
        }
    }

    @Test
    void createUser_ShouldSetCorrectDefaultValues() {
        // Given
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setPassword("plainPassword");
        newUser.setRoles(new HashSet<>());

        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        // When
        userService.createUser(newUser);

        // Then
        assertTrue(newUser.isActive());
        assertEquals("encodedPassword", newUser.getPassword());
        assertEquals(1, newUser.getRoles().size());
        assertTrue(newUser.getRoles().contains(Role.ROLE_USER));
    }
}