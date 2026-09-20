package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.DTO.EmailEvent;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Repository.UserRepo;
import com.arun.Restaurantbackend.Utilis.EmailType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.Only;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.Mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

@SpringBootTest
class UserServiceTest {

    @Autowired
    EmailProducer emailProducer;


    @Test
    void sendEmail(){

        EmailEvent emailEvent=new EmailEvent();
        emailEvent.setType(EmailType.OTP);
        emailEvent.setTo("arunrajput16177@gmail.com");

        emailProducer.produce(emailEvent);

    }


    }