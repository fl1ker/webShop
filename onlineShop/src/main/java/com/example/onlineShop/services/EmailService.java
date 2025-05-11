package com.example.onlineShop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPurchaseConfirmation(String to, String productName, int quantity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Подтверждение покупки");
        message.setText("Спасибо за покупку!\n\n" +
                "Вы приобрели: " + productName + "\n" +
                "Количество: " + quantity + "\n\n" +
                "С уважением, команда магазина");
        mailSender.send(message);
    }
}

