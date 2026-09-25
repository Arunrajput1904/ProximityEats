package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.DTO.EmailEvent;
import com.arun.Restaurantbackend.DTO.ProcessedWebhook;
import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.BadRequestException;
import com.arun.Restaurantbackend.Exception.ConflictException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.EmailType;
import com.arun.Restaurantbackend.Utilis.SubscriptionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepo subRepo;
    private final IdempotencyKeyRepository keyRepo;
    private final AuditLogRepository auditRepo;
   private final EmailProducer emailProducer;
   private final UserRepo userRepo;
    private final ProcessWebhookRepo processWebhookRepo;
    private final PlanRepository planRepository;
    private final Validationhandler validationhandler;


    @Transactional
    public Subscription processCheckout(String idempotencyKey, Long planId) {

//        Validationhandler validationhandler1 = validationhandler;
        User finduser = validationhandler.finduser();


        User user=userRepo.findById(finduser.getId()).orElseThrow(()-> new BadRequestException("User id does not match"));

        try {
            IdempotencyKey key = new IdempotencyKey();
            key.setIdempotencyKey(idempotencyKey);
            key.setLockedAt(LocalDateTime.now());
            keyRepo.saveAndFlush(key);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("DUPLICATE_REQUEST");
        }



        String mockGatewayId = "sub_" + UUID.randomUUID().toString().substring(0, 8);

        Plan invalidPlainId = planRepository.findById(planId).orElseThrow(() -> new ResourceNoFoundException("Invalid Plain id "));

        Subscription sub = new Subscription();
        sub.setUserId(user.getId());
        sub.setPlanId(planId);
        sub.setGatewaySubId(mockGatewayId);
        sub.setPlan(invalidPlainId);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setStartDate(LocalDate.now());
        sub.setNextBillingDate(LocalDate.now());
        sub = subRepo.save(sub);
log.info(" Message Createdddd............................................................................");

        AuditLog log = new AuditLog();
        log.setSubscriptionId(sub.getId());
        log.setAction("SUBSCRIBED");
        log.setTimestamp(LocalDateTime.now());
        auditRepo.save(log);


        EmailEvent emailEvent=new EmailEvent();
        emailEvent.setType(EmailType.SUBSCRIPTION_CREATED);
        emailEvent.setUser(user);

        emailProducer.produce(emailEvent);

        return sub;
    }


    @Transactional
    public void cancelUserSubscription(Long id){


        User finduser = validationhandler.finduser();
        Subscription sub=subRepo.findByUserIdAndSubId(finduser.getId(),id).orElseThrow(()-> new BadRequestException("Subscription is not found"));
        if (sub.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new ConflictException("Subscription is not active.");
        }
        sub.setStatus(SubscriptionStatus.CANCELLING);
        AuditLog auditLog=new AuditLog();
        auditLog.setSubscriptionId(sub.getId());
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setAction("SUBSCRIPTION_CANCELLED");
        auditRepo.save(auditLog);
    }


    @Transactional
    public void processWebhook(String eventId, String eventType, String gatewaySubId) {
        System.out.println("processWebhokkkkkkkkk.............................");
        Optional<ProcessedWebhook>existing=processWebhookRepo.findByEventIdForUpdate(Long.valueOf(eventId));

        if(existing.isPresent()){
            return;
//            throw new ConflictException("Exist already");
        }
        ProcessedWebhook webhook = new ProcessedWebhook(Long.valueOf(eventId), "PROCESSING", LocalDateTime.now());
        processWebhookRepo.save(webhook);

        Subscription sub = subRepo.findByGatewaySubId(gatewaySubId)
                .orElseThrow(() -> new RuntimeException("Subscription not found for gateway ID: " + gatewaySubId));
       User user=userRepo.findById(sub.getUserId()).orElse(null);
       if(user==null){
           return;
       }
        if ("invoice.paid".equals(eventType)) {
            sub.setStatus(SubscriptionStatus.ACTIVE);
            sub.setNextBillingDate(sub.getNextBillingDate().plusMonths(1));
            subRepo.save(sub);
            EmailEvent emailEvent=new EmailEvent();
            emailEvent.setUser(user);
            emailEvent.setType(EmailType.SUBSCRIPTION_RENEW);
            emailProducer.produce(emailEvent);
        } else if ("invoice.payment_failed".equals(eventType)) {
            sub.setStatus(SubscriptionStatus.PAST_DUE);
            subRepo.save(sub);
        }

        webhook.setStatus("COMPLETED");
        processWebhookRepo.save(webhook);
    }
}
