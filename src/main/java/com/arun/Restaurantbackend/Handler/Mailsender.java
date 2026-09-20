package com.arun.Restaurantbackend.Handler;

import com.arun.Restaurantbackend.DTO.Otp;
import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Repository.BoyEmailRepo;
import com.arun.Restaurantbackend.Repository.EmailvalidationRepo;
import com.arun.Restaurantbackend.Repository.GroupCartRepo;
import com.arun.Restaurantbackend.Repository.GroupOtpTrackRepo;
import com.arun.Restaurantbackend.Service.Validationhandler;
import com.arun.Restaurantbackend.Utilis.EmailType;
import com.arun.Restaurantbackend.Utilis.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class Mailsender {

    private final Validationhandler validationhandler;
    private final JavaMailSender javaMailSender;


private final EmailvalidationRepo emailvalidationRepo;


private final BoyEmailRepo boyEmailRepo;


    String passwordgentor="abcdefghijklmnopqrstuvwxyz123456789@#!%$&*";


 char [] passarray= passwordgentor.toCharArray();
    int length=passwordgentor.length();
    private final GroupCartRepo groupCartRepo;
    private final GroupOtpTrackRepo groupOtpTrackRepo;


    public  void sendOtp(String to){


        StringBuilder password=new StringBuilder();

        for(int i=0; i<6; i++){
            password.append(passarray[(int) (Math.random() * length)]);
        }

        User user=validationhandler.finduser();

        Emailvalidation emailvalidation;
        System.out.println("........................"+length);

            emailvalidation=Emailvalidation.builder().email(to).localDateTime(LocalDateTime.now().plusMinutes(5)).GeneratedPassword(password.toString()).build();

        System.out.println("..........................."+length);


        SimpleMailMessage m = new SimpleMailMessage();
        m.setTo(to);
        m.setSubject("Reset Password OTP");
        m.setText("Your OTP: " + password + " (valid 5 minutes)");
        javaMailSender.send(m);
        emailvalidationRepo.deleteAllByEmail(to);
        emailvalidationRepo.save(emailvalidation);

    }

    public void sendConfirmationmessage(String to, Order order,String message){
        SimpleMailMessage m = new SimpleMailMessage();
        m.setTo(to);
        m.setSubject("Order "+message);
        m.setSubject("Your order Detail");
        m.setText(" Order  -> " +order);
        javaMailSender.send(m);
    }



    @Async
    public void sendOtpGroupUser(String to, Long userid, GroupCart groupCart){

        SimpleMailMessage message=new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Order Validation Otp");
        message.setSubject("Your otp");

        StringBuilder password=new StringBuilder();


        for(int i=0; i<6; i++){
            password.append(passarray[(int) (Math.random() * length)]);
        }

       GroupOtpTrack track=new GroupOtpTrack();
        track.setOtp(password.toString());
        track.setGroupCart(groupCart);
        track.setUserid(userid);
        groupOtpTrackRepo.save(track);
message.setText("Otp : "+password.toString());
        javaMailSender.send(message);
    }



    public void sendotptwoway(Order order, User user) {
        StringBuilder password=new StringBuilder();
        for(int i=0; i<6; i++){
            password.append(passarray[(int) (Math.random() * length)]);
        }
        Deliveryboyemail deliveryboyemail=boyEmailRepo.findById(user.getId()).orElse(null);
        Deliveryboyemail emailvalidation=Deliveryboyemail.builder().currentpassword(password.toString()).userid(user.getId()).build();
        if(deliveryboyemail!=null){
            emailvalidation.setCount(deliveryboyemail.getCount()+1);
        }
        SimpleMailMessage m = new SimpleMailMessage();
        m.setTo(order.getUser().getEmail());
        m.setSubject("Order outofdelivery");
        m.setSubject("Your order Detail");
        m.setText(" Order  -> " +order);
        javaMailSender.send(m);
emailvalidation.setCount(0l);
        boyEmailRepo.deletepervious(user.getId());

        boyEmailRepo.save(emailvalidation);
    }


    public void sendUserMembershipStatus(User user, EmailType emailType){


     if(emailType.equals(EmailType.SUBSCRIPTION_CREATED)){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Your Membership is Active – Enjoy Your Perks!");
        message.setText("\n" +
                "      Hi "+user.getName()+" ,\n" +
                "\n" +
                "  Your recurring membership mandate has been successfully set up.\n" +
                " Your trial period has started, and your first automated billing will take place on "+LocalDateTime.now().plusDays(30)+"\n" +
                "\n" +
                "Enjoy zero delivery fees and exclusive perks!\n" +
                "\n" +
                " Best  , The Food Delivery Team");

       message.setTo(user.getEmail());

       javaMailSender.send(message);
    }
     else{
         SimpleMailMessage message = new SimpleMailMessage();
         message.setSubject("Your Membership is RENEWED – Enjoy Your Perks!");
         message.setText("\n" +
                 "      Hi "+user.getName()+" ,\n" +
                 "\n" +
                 "  Your recurring membership mandate has been successfully renew up.\n" +
                 " Your trial period has started, and your first automated billing will take place on "+LocalDateTime.now().plusDays(30)+"\n" +
                 "\n" +
                 "Enjoy zero delivery fees and exclusive perks!\n" +
                 "\n" +
                 " Best  , The Food Delivery Team");
         message.setTo(user.getEmail());
         javaMailSender.send(message);
     }



}

    public void sendUserRemainderEmail(User user) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Your Membership is RENEWED ");
        message.setText("\n" +
                "      Hi "+user.getName()+" ,\n" +
                "\n" +
                "  Your recurring membership mandate has been renewed in 3 days.\n" +
                "if you want to make any update of subscription , Please try to update before 3 days\n"+
                " Best  , The Food Delivery Team");
        message.setTo(user.getEmail());
        javaMailSender.send(message);

    }



    }
