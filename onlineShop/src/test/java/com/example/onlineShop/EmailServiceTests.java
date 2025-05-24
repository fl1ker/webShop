package com.example.onlineShop;

import com.example.onlineShop.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTests {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    private String testEmail;
    private String testProductName;
    private int testQuantity;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testProductName = "Testowy Produkt";
        testQuantity = 2;
    }

    @Test
    @DisplayName("Powinien wysłać email z potwierdzeniem zakupu")
    void shouldSendPurchaseConfirmationEmail() {
        // when
        emailService.sendPurchaseConfirmation(testEmail, testProductName, testQuantity);

        // then
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertEquals(testEmail, capturedMessage.getTo()[0]);
        assertEquals("Подтверждение покупки", capturedMessage.getSubject());

        String expectedText = "Спасибо за покупку!\n\n" +
                "Вы приобрели: " + testProductName + "\n" +
                "Количество: " + testQuantity + "\n\n" +
                "С уважением, команда магазина";
        assertEquals(expectedText, capturedMessage.getText());
    }

    @Test
    @DisplayName("Powinien obsłużyć pusty email")
    void shouldHandleEmptyEmail() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation("", testProductName, testQuantity)
        );
        assertEquals("Adres email nie może być pusty", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Powinien obsłużyć null jako email")
    void shouldHandleNullEmail() {
        // when & then
        assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(null, testProductName, testQuantity)
        );
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Powinien obsłużyć pusty nazwa produktu")
    void shouldHandleEmptyProductName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(testEmail, "", testQuantity)
        );
        assertEquals("Nazwa produktu nie może być pusta", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Powinien obsłużyć negatywną ilość")
    void shouldHandleNegativeQuantity() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(testEmail, testProductName, -1)
        );
        assertEquals("Ilość musi być większa niż zero", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Powinien obsłużyć zerową ilość")
    void shouldHandleZeroQuantity() {
        // when & then
        assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendPurchaseConfirmation(testEmail, testProductName, 0)
        );
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Powinien obsłużyć błąd wysyłania maila")
    void shouldHandleMailSendingError() {
        // given
        doThrow(new RuntimeException("Mail sending failed"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // when & then
        assertThrows(
                RuntimeException.class,
                () -> emailService.sendPurchaseConfirmation(testEmail, testProductName, testQuantity)
        );
    }
}