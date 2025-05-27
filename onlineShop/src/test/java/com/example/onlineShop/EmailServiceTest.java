package com.example.onlineShop;

import com.example.onlineShop.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender);
    }

    @Test
    void shouldSendPurchaseConfirmationWithValidData() {
        // Given
        String email = "customer@example.com";
        String productName = "Laptop Dell XPS";
        int quantity = 2;

        // When
        emailService.sendPurchaseConfirmation(email, productName, quantity);

        // Then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(email, sentMessage.getTo()[0]);
        assertEquals("Purchase Confirmation", sentMessage.getSubject());

        String expectedText = "Thank you for your purchase!\n\n" +
                "You bought: " + productName + "\n" +
                "Quantity: " + quantity + "\n\n" +
                "Best regards, Team webShop";
        assertEquals(expectedText, sentMessage.getText());
    }

    @Test
    void shouldSendPurchaseConfirmationWithMinimalQuantity() {
        // Given
        String email = "test@example.com";
        String productName = "Mouse";
        int quantity = 1;

        // When
        emailService.sendPurchaseConfirmation(email, productName, quantity);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        String email = null;
        String productName = "Laptop";
        int quantity = 1;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(email, productName, quantity)
        );

        assertEquals("Adres email nie może być pusty", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        // Given
        String email = "";
        String productName = "Laptop";
        int quantity = 1;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(email, productName, quantity)
        );

        assertEquals("Adres email nie może być pusty", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsWhitespace() {
        // Given
        String email = "   ";
        String productName = "Laptop";
        int quantity = 1;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(email, productName, quantity)
        );

        assertEquals("Adres email nie może być pusty", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNameIsNull() {
        // Given
        String email = "customer@example.com";
        String productName = null;
        int quantity = 1;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(email, productName, quantity)
        );

        assertEquals("Nazwa produktu nie może być pusta", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNameIsEmpty() {
        // Given
        String email = "customer@example.com";
        String productName = "";
        int quantity = 1;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(email, productName, quantity)
        );

        assertEquals("Nazwa produktu nie może być pusta", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNameIsWhitespace() {
        // Given
        String email = "customer@example.com";
        String productName = "   ";
        int quantity = 1;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(email, productName, quantity)
        );

        assertEquals("Nazwa produktu nie może być pusta", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        // Given
        String email = "customer@example.com";
        String productName = "Laptop";
        int quantity = 0;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(email, productName, quantity)
        );

        assertEquals("Ilość musi być większa niż zero", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // Given
        String email = "customer@example.com";
        String productName = "Laptop";
        int quantity = -1;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(email, productName, quantity)
        );

        assertEquals("Ilość musi być większa niż zero", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldHandleSpecialCharactersInProductName() {
        // Given
        String email = "customer@example.com";
        String productName = "Смартфон iPhone 15 Pro Max 256GB";
        int quantity = 1;

        // When
        emailService.sendPurchaseConfirmation(email, productName, quantity);

        // Then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage.getText().contains(productName));
    }

    @Test
    void shouldHandleLargeQuantity() {
        // Given
        String email = "customer@example.com";
        String productName = "Pen";
        int quantity = Integer.MAX_VALUE;

        // When
        emailService.sendPurchaseConfirmation(email, productName, quantity);

        // Then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage.getText().contains(String.valueOf(quantity)));
    }

    @Test
    void shouldVerifyEmailMessageStructure() {
        // Given
        String email = "test@domain.com";
        String productName = "Gaming Chair";
        int quantity = 3;

        // When
        emailService.sendPurchaseConfirmation(email, productName, quantity);

        // Then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();

        // Verify all components are present
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().length);
        assertNotNull(message.getSubject());
        assertNotNull(message.getText());

        // Verify content structure
        String text = message.getText();
        assertTrue(text.contains("Thank you for your purchase!"));
        assertTrue(text.contains("You bought: " + productName));
        assertTrue(text.contains("Quantity: " + quantity));
        assertTrue(text.contains("Best regards, Team webShop"));
    }
}