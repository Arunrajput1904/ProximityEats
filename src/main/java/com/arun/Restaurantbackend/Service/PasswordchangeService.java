package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.DTO.EmailEvent;
import com.arun.Restaurantbackend.DTO.changepassword;
import com.arun.Restaurantbackend.Entity.Emailvalidation;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Exception.InsufficientAuthenticationException;

import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Handler.Mailsender;
import com.arun.Restaurantbackend.Repository.EmailvalidationRepo;
import com.arun.Restaurantbackend.Repository.UserRepo;
import com.arun.Restaurantbackend.Utilis.EmailType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordchangeService {

    final private Mailsender mailsender;
    private final EmailvalidationRepo emailvalidationRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
private final EmailProducer producer;


    @Transactional
    public Boolean changepassword(changepassword user)  {


        Emailvalidation emailvalidation=emailvalidationRepo.findByEmail(user.getEmail()).orElseThrow(()-> new ResourceNoFoundException("Not found in db"));

        User user1=userRepo.findByEmail(user.getEmail()).orElseThrow(()->new BadCredentialsException("Not found"));
        if(emailvalidation.getGeneratedPassword().equals(user.getOtp()) && emailvalidation.getLocalDateTime().isAfter(LocalDateTime.now())){
            emailvalidationRepo.deleteById(emailvalidation.getId());
            user1.setPassword(passwordEncoder.encode(user.getSetNewPassword()));
            return true;
        }
        return false;

    }


    public void sendemailotp(String email) {
        User user=userRepo.findByEmail(email).orElseThrow(()-> new InsufficientAuthenticationException("Not found in db"));
        if(user.getPassword()==null){
            throw new InsufficientAuthenticationException("Connect with google");
        }


        EmailEvent emailEvent=new EmailEvent();
        emailEvent.setTo(email);
        emailEvent.setType(EmailType.OTP);
        producer.produce(emailEvent);

    }
}
