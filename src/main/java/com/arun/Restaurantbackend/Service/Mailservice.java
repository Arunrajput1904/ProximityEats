package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.DTO.EmailEvent;
import com.arun.Restaurantbackend.Entity.Order;
import com.arun.Restaurantbackend.Handler.Mailsender;
import com.arun.Restaurantbackend.Utilis.EmailType;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class Mailservice {


    private final Mailsender mailsender;

    private final EmailProducer producer;

    void sendmailofOrder( String email, Order order){
        EmailEvent emailEvent=new EmailEvent();

        emailEvent.setOrder(order);
        emailEvent.setType(EmailType.CONFIRMATION);
        emailEvent.setTo(email);

        producer.produce(emailEvent);
    }




}
