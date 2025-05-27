package com.example.onlineShop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPurchaseConfirmation(String to, String productName, int quantity) {
        // Walidacja adresu email
        if (!StringUtils.hasText(to)) {
            throw new IllegalArgumentException("Adres email nie może być pusty");
        }

        // Walidacja nazwy produktu
        if (!StringUtils.hasText(productName)) {
            throw new IllegalArgumentException("Nazwa produktu nie może być pusta");
        }

        // Walidacja ilości
        if (quantity <= 0) {
            throw new IllegalArgumentException("Ilość musi być większa niż zero");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Purchase Confirmation");
        message.setText("Thank you for your purchase!\n\n" +
                "You bought: " + productName + "\n" +
                "Quantity: " + quantity + "\n\n" +
                "Best regards, Team webShop");
        mailSender.send(message);
    }
}

