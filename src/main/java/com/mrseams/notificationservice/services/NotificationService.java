package com.mrseams.notificationservice.services;
import com.mrseams.avro.OrderPlacedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class NotificationService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @KafkaListener(topics = "order-placed")
    public void listen(OrderPlacedEvent OPE){
        log.info("OrderPlacedEvent received {}", OPE);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(OPE.getEmail().toString());
            helper.setSubject("Order Confirmation");
            helper.setText("Your order " + OPE.getOrderNumber() + " has been placed successfully!");

            mailSender.send(message);
            log.info("Order confirmation email sent to {}", OPE.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send email notification", e);
        }
    }
}
