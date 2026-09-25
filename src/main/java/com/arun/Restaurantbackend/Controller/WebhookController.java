package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.Service.SubscriptionService;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Hidden
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "16. Webhook Apis")
public class WebhookController {



    private final SubscriptionService subscriptionService;

    @Value("${RAZOR_PAY_SECRET}")
    private String webhook_secret;


    @PostMapping("/razorpay")
    public ResponseEntity<String> handleWebhook(@RequestBody String rawPayload
                                     , @RequestHeader("X-Razorpay-Signature") String incomingSignature){
log.info("razorpay have to be implemented .....................");
        try{
            boolean isvalid= Utils.verifyWebhookSignature(rawPayload,incomingSignature,webhook_secret);

            if (!isvalid) {
                log.warn("Invalid Razorpay webhook signature detected!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
            }

            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode=mapper.readTree(rawPayload);


            String eventId = rootNode.get("event_id").asString();
            String eventType = rootNode.get("event").asString();


            JsonNode subscriptionObj = rootNode.path("payload")
                    .path("subscription")
                    .path("entity");
            String gatewaySubId = subscriptionObj.get("id").toString();

            subscriptionService.processWebhook(eventId, eventType, gatewaySubId);

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (RazorpayException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("webhook error");
        }
    }

}
