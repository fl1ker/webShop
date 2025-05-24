package com.example.onlineShop;

import com.example.onlineShop.models.User;
import com.example.onlineShop.models.enums.Role;
import com.example.onlineShop.repositories.UserRepository;
import com.example.onlineShop.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

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
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setActive(true);
        testUser.setRoles(new HashSet<>());
    }

    @Test
    @DisplayName("Powinien utworzyć nowego użytkownika")
    void shouldCreateNewUser() {
        // given
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";
        testUser.setPassword(rawPassword);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        boolean result = userService.createUser(testUser);

        // then
        assertTrue(result);
        assertTrue(testUser.isActive());
        assertTrue(testUser.getRoles().contains(Role.ROLE_USER));
        verify(userRepository).save(testUser);
        verify(passwordEncoder).encode(rawPassword); // Weryfikujemy enkodowanie oryginalnego hasła
        assertEquals(encodedPassword, testUser.getPassword()); // Sprawdzamy czy zakodowane hasło zostało ustawione
    }

    @Test
    @DisplayName("Nie powinien utworzyć użytkownika z istniejącym emailem")
    void shouldNotCreateUserWithExistingEmail() {
        // given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

        // when
        boolean result = userService.createUser(testUser);

        // then
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Powinien zwrócić listę wszystkich użytkowników")
    void shouldListAllUsers() {
        // given
        List<User> expectedUsers = List.of(testUser);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // when
        List<User> result = userService.list();

        // then
        assertEquals(expectedUsers, result);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Powinien zbanować aktywnego użytkownika")
    void shouldBanActiveUser() {
        // given
        testUser.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        userService.banUser(1L);

        // then
        assertFalse(testUser.isActive());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Powinien odbanować zbanowanego użytkownika")
    void shouldUnbanBannedUser() {
        // given
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        userService.banUser(1L);

        // then
        assertTrue(testUser.isActive());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Powinien zmienić role użytkownika")
    void shouldChangeUserRoles() {
        // given
        Map<String, String> form = new HashMap<>();
        form.put(Role.ROLE_ADMIN.name(), "on");
        form.put("_csrf", "token"); // symulacja pola csrf

        // when
        userService.changeUserRoles(testUser, form);

        // then
        assertTrue(testUser.getRoles().contains(Role.ROLE_ADMIN));
        assertEquals(1, testUser.getRoles().size());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Powinien zwrócić użytkownika na podstawie Principal")
    void shouldGetUserByPrincipal() {
        // given
        when(principal.getName()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

        // when
        User result = userService.getUserByPrincipal(principal);

        // then
        assertEquals(testUser, result);
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("Powinien zwrócić nowego użytkownika gdy Principal jest null")
    void shouldReturnNewUserWhenPrincipalIsNull() {
        // when
        User result = userService.getUserByPrincipal(null);

        // then
        assertNotNull(result);
        assertTrue(result instanceof User);
        verify(userRepository, never()).findByEmail(any());
    }
}