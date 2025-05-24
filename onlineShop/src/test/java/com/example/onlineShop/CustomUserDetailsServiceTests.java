package com.example.onlineShop;

import com.example.onlineShop.models.User;
import com.example.onlineShop.repositories.UserRepository;
import com.example.onlineShop.services.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;
    private final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword("hashedPassword123");
        testUser.setActive(true);
    }

    @Test
    @DisplayName("Powinien znaleźć użytkownika po emailu")
    void shouldLoadUserByUsername() {
        // given
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(testUser);

        // when
        UserDetails foundUser = userDetailsService.loadUserByUsername(TEST_EMAIL);

        // then
        assertNotNull(foundUser);
        assertEquals(TEST_EMAIL, foundUser.getUsername());
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek gdy użytkownik nie zostanie znaleziony")
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(null);

        // when & then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(nonExistentEmail)
        );

        assertEquals("User not found with email: " + nonExistentEmail, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(nonExistentEmail);
    }

    @Test
    @DisplayName("Powinien obsłużyć null jako parametr email")
    void shouldHandleNullEmail() {
        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(null)
        );
    }

    @Test
    @DisplayName("Powinien obsłużyć pusty string jako email")
    void shouldHandleEmptyEmail() {
        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("")
        );
    }
}