package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.DTO.EmailEvent;
import com.arun.Restaurantbackend.Handler.Mailsender;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumer {

    private final Mailsender mailsender;

    @KafkaListener(topics = "email-event"
            ,groupId = "email-consumer")
    void consumer(EmailEvent emailEvent){
        System.out.println("========== CONSUMER CALLED ==========");
        System.out.println(emailEvent);
        switch (emailEvent.getType()){

            case OTP :
                mailsender.sendOtp(emailEvent.getTo());
                break;
            case CONFIRMATION:
                mailsender.sendConfirmationmessage(emailEvent.getTo(),emailEvent.getOrder(),emailEvent.getMessage());
                break;
            case DELIVERY_OTP:
                mailsender.sendotptwoway(emailEvent.getOrder(),emailEvent.getUser());
                break;
            case SUBSCRIPTION_CREATED, SUBSCRIPTION_RENEW:
                mailsender.sendUserMembershipStatus(emailEvent.getUser(),emailEvent.getType());
                break;
            case SUBSCRIPTION_REMAINDER:
                mailsender.sendUserRemainderEmail(emailEvent.getUser());
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown email event type: " + emailEvent.getType()
                );
        }


    }



}
