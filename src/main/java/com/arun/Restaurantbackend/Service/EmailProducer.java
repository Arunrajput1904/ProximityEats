package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.DTO.EmailEvent;
import com.arun.Restaurantbackend.Utilis.EmailType;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailProducer {


    private final KafkaTemplate<String, EmailEvent> kafkaTemplate;




    public void produce(EmailEvent evt){

        kafkaTemplate.send("email-event",evt.getType().name(),evt);
    }





}
