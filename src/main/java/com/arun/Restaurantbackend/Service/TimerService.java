package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.DTO.EmailEvent;

import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Entity.OrderBundle;
import com.arun.Restaurantbackend.Entity.Stop;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.SslInfo;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimerService {
    private final AuditLogRepository auditLogRepository;
    private final StopRepo stopRepo;
    private final SocietyEdgeRepo societyEdgeRepo;
    private final UserRepo userRepo;
    private final SubscriptionRepo subscriptionRepo;
private  final SocietyNRepo societyNRepo;
    private final GroupOtpTrackRepo groupOtpTrackRepo;
    private final OrderRepo orderRepo;
    private  final GroupCartRepo groupCartRepo;
    private final MakeGroupOrderService makeGroupOrder;
private final ServerBrokerConnection serverBrokerConnection;
    private final RestaurantRepo restaurantRepo;
    private final DeliveryBoyRepo deliveryBoyRepo;
private final DELIVERYASSIGNSERVICE deliveryassignservice;
private final EmailProducer emailProducer;
private final Validationhandler validationhandler;
private final BundleRepo bundleRepo;
private final BundleService bundleService;

    @Value("${RAZOR_PAY_KEY}")
    String razorKeyId;

    @Value("${RAZOR_PAY_SECRET}")
    String razorSecretKey;


    @Scheduled(cron = "0/15 * * * * *")
    void getExcuted(){
        groupCartRepo.findAll().stream().filter(x-> x.getExpiresAt().isBefore(LocalDateTime.now())
                && x.getStatus()==GroupCartStatus.ACTIVE).forEach(x-> makeGroupOrder.makeOrder(x) );
        groupCartRepo.findAll().stream().filter(x-> x.getPaymentAt().isBefore(LocalDateTime.now())
                && x.getStatus()==GroupCartStatus.LOCKED).forEach(max->{
                     makeGroupOrder.OrderConfirmed(max);
            System.out.println(max);
        });
    }




    @Scheduled(cron = "0/5 * * * * *")
    void getpublishGroupOrdertoUser(){
        groupCartRepo.findByStatus(GroupCartStatus.ACTIVE)
                .stream().forEach(x-> serverBrokerConnection.pushtheOrder(x));
    }



    @Scheduled(cron = "0/30 * * * * *")
    void changethestautusofRestaurantOnthebasisofDeliveryBoy(){

        List<Restaurant>list=restaurantRepo.findAll();

        for(Restaurant restaurant : list){
             if(!restaurant.getStatus().equals(String.valueOf(StatusEnum.PENDING)) && !restaurant.getStatus().equals(String.valueOf(StatusEnum.UNAVAILABLE))){
                Long val=getNearestDeliveryBoy(restaurant);
                if(val>15){
                    restaurant.setStatus(String.valueOf(StatusEnum.INACTIVE));
                }
                else{
                    restaurant.setStatus(String.valueOf(StatusEnum.ACTIVE));
                }
            }


                   restaurantRepo.save(restaurant);
        }


    }


    Long getNearestDeliveryBoy(Restaurant restaurant){

        List<DeliveryBoy>deliveryBoys=deliveryBoyRepo.findByStatus(StatusEnum.AVAILABLE);

        Long val=11l;
        for(DeliveryBoy deliveryBoy : deliveryBoys){
            Double findkm = deliveryassignservice.findkm(deliveryBoy.getTown(), restaurant.getTown());
            val=Math.min(val, findkm.longValue());
            if(val<=15l){
                return val;
            }
        }
        return val;
    }

    @Scheduled(cron = "0 */15 * * * *")
    void makeGroupCartFails(){

        orderRepo.findAll()
                .stream()
                .filter(x->
                        x.getStatus().equals(OrderEnum.DELIVERED) && x.getOrderType().equals(OrderType.GROUP) && x.getOrderAcceptTime().isBefore(LocalDateTime.now().minusMinutes(15))
                )
                .map(x-> x.getId())
                .map(x->  {
                    Optional<GroupCart> byOrderId = groupCartRepo.findByOrderId(x);
                return byOrderId;
                })
                .filter(x-> x.isPresent())
                .map(x-> x.get())
                .forEach(x-> {

              List<GroupOtpTrack>list=groupOtpTrackRepo.findbyGroupid(x.getId());

                boolean flag=false;

                for(GroupOtpTrack group : list){
                    if(!group.isSuccess()){
                        flag=true;
                        break;
                    }
                }
                   if(flag) {
                       x.setStatus(GroupCartStatus.CANCELLED);
                   }
                });


    }

    @Scheduled(cron = "5 0 0 * * *")
    public void makeRemainderToUser(){


        List<Subscription> subscriptionRepoAll = subscriptionRepo.findAll();

        subscriptionRepoAll.stream()
                .filter(x-> x.getStatus().equals(SubscriptionStatus.ACTIVE)
                        && x.getNextBillingDate().isEqual(LocalDate.now().plusDays(3)))
                .forEach(x->{
                    User user = userRepo.findById(x.getUserId()).orElse(null);
                     if(user!=null) {
                         EmailEvent emailEvent = new EmailEvent();
                         emailEvent.setUser(user);
                         emailEvent.setType(EmailType.SUBSCRIPTION_REMAINDER);
                         emailProducer.produce(emailEvent);
                     }
                                    });


    }

    @Scheduled(cron = "0/30 * * * * *")
    public void triggerRenewalCharges() throws Exception {
        List<Subscription> dueSubscriptions = subscriptionRepo.findByNextBillingAndstatus(LocalDate.now(), SubscriptionStatus.ACTIVE);
        RestClient restClient = RestClient.create();

        for (Subscription sub : dueSubscriptions) {
            try {
                User user = userRepo.findById(sub.getUserId()).orElse(null);
                if (user == null) continue;

                Double cost = sub.getPlan().getPrice();

                    user.getWallet().setBalance(user.getWallet().getBalance() - cost);
                    userRepo.save(user);


                    String generatedRazorpayId = "N/A";
                    try {
                        Map<String, Object> orderRequest = new HashMap<>();
                        orderRequest.put("amount", cost * 100);
                        orderRequest.put("currency", "INR");
                        orderRequest.put("receipt", "wallet_txn_" + sub.getId() + "_" + LocalDate.now());

                        ResponseEntity<Map> orderRes = restClient.post()
                                .uri("https://api.razorpay.com/v1/orders")
                                .headers(x -> x.setBasicAuth(razorKeyId, razorSecretKey))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(orderRequest)
                                .retrieve()
                                .toEntity(Map.class);

                        generatedRazorpayId = orderRes.getBody().get("id").toString();
                    } catch (Exception e) {
                        log.error("Razorpay ID generation failed, but wallet was deducted. Error: {}", e.getMessage());
                    }


                    sub.setNextBillingDate(sub.getNextBillingDate().plusMonths(1));
                    subscriptionRepo.save(sub);


                    AuditLog logRecord = new AuditLog();
                    logRecord.setSubscriptionId(sub.getId());
                    logRecord.setAction("WALLET_RENEWAL_SUCCESS");
                    logRecord.setRazorpayOrderId(generatedRazorpayId);
                    logRecord.setTimestamp(LocalDateTime.now());
                    auditLogRepository.save(logRecord);

                    log.info("Wallet charged. Razorpay ID: {}", generatedRazorpayId);

                    EmailEvent emailEvent = new EmailEvent();
                    emailEvent.setType(EmailType.SUBSCRIPTION_RENEW);
                    emailEvent.setUser(user);
                    emailProducer.produce(emailEvent);

                }
            catch (Exception e){
                throw new Exception("Error Occured....");
            }
        }

    }




    @Scheduled(cron = "0/5 * * * * *")
    void makeOrderAsBundle(){

        List<Order>preparedOrder=orderRepo.findByStatus(OrderEnum.PREPARING);

        Map<Long, List<Order>> collect = preparedOrder
                .stream().filter(x -> x.getOrderType()!=OrderType.BUNDLE)
                .filter(x -> x.getLastUpdateTime().isAfter(LocalDateTime.now().minusMinutes(3)))
                .collect(Collectors.groupingBy(x -> x.getRestaurant().getId()));


        for(Map.Entry<Long,List<Order>>mp : collect.entrySet()) {

            Map<String, List<Order>> collect1 = mp.getValue().stream()
                    .collect(Collectors.groupingBy(x -> {
                        String societyName = x.getAddress().getSocietyName();
                        return societyNRepo.findBySocietyName(societyName).get().getZoneName();
                    }));


            for (Map.Entry<String, List<Order>> ss : collect1.entrySet()) {
                      if (ss.getValue().size()<2){
                          continue;
                      }
                boolean flag = false;
                for (Bundle bundle : bundleRepo.findAll()) {

                    if (bundle.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(3)) && bundle.getZoneName().equals(ss.getKey())) {
                        flag = true;
                        bundle.setRestaurant(restaurantRepo.findById(mp.getKey()).orElse(null));
                        bundleService.addtothebundle(bundle, ss.getValue());
                        break;
                    }
                }
                if (!flag) {
                    log.info("add to the bundle "+ ss.getValue().size());
                    bundleService.createnewBundle(mp.getKey(),ss.getKey(), ss.getValue());
                }
            }
        }


    }


    @Scheduled(cron = "0/5 * * * * * ")
    @Transactional
    void removeCancelTheBundle () {

        List<Bundle> byStatus = bundleRepo.findAll()
                .stream()
                .filter(x-> !(x.getStatus().equals(BundleStatus.DELIVERED) && x.getStatus().equals(BundleStatus.CANCELLED) ))
                .toList();


        List<Bundle> bundleStream = byStatus.stream()
                .filter(x->{
                    if(x.getLastUpdateTime().isBefore(LocalDateTime.now().minusMinutes(10))){
                        x.setStatus(BundleStatus.CANCELLED);
                         return false;
                    }
                    return true;
                })
                .toList();

        bundleStream.stream()
                .filter(x->!x.getStatus().equals(BundleStatus.OUT_OF_DELIVERY))
                .forEach(x->{
                    List<OrderBundle>list=x.getOrderBundles();
                    List<OrderBundle>cancelorder=          list.stream()
                            .filter(xx-> xx.getOrder().getCancel()!=null)
                            .toList();

                    log.info(cancelorder+" .................cancel Order");
                    list.removeAll(cancelorder);
                    String town = x.getRestaurant().getTown();
                    Double price = x.getPrice();
if(!cancelorder.isEmpty()){

    bundleService.bundleupdationOrOrderDynamicchange(x,town);
    x.setPrice(price);

}
                });

        bundleStream.stream()
                .filter(x->x.getStatus().equals(BundleStatus.OUT_OF_DELIVERY))
                .forEach(x->{
                    List<OrderBundle>list=x.getOrderBundles();
                    List<OrderBundle>cancelorder=          list.stream()
                            .filter(xx-> xx.getOrder().getCancel()!=null)
                            .toList();

                    list.removeAll(cancelorder);

                    if(list.isEmpty()){

                        x.getDeliveryBoy().getUser().getWallet().addmoney(25.0);

                        return;
                    }


                    String town = x.getRestaurant().getTown();
                    Double price = x.getPrice();

                    bundleService.bundleupdationOrOrderDynamicchange(x,town);
                    x.setPrice(price);
                });


    }


    @Scheduled(cron = "0/5 * * * * * ")
    @Transactional
    void moneyCreditedUser(){
        List<Bundle> byStatus = bundleRepo.findByStatus(BundleStatus.DELIVERED);
        byStatus.stream()
                .forEach(x->{
                    x.getDeliveryBoy().getUser().getWallet().addmoney(x.getPrice());
                });
    }




    @Scheduled(cron = "* 5 * * * *")
    public void keepAlive() {
         RestClient.create()
        .get()
                .uri("https://proximityeats.onrender.com/actuator/health")
                .retrieve()
                .toBodilessEntity();

        System.out.println("Keep alive request sent");
    }

}
