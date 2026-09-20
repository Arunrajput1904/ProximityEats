package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.DTO.DemoRestaurant;
import com.arun.Restaurantbackend.DTO.ItemDto;
import com.arun.Restaurantbackend.DTO.OrderDto;
import com.arun.Restaurantbackend.DTO.RestaurantDto;
import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.*;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.ItemAction;
import com.arun.Restaurantbackend.Utilis.OrderEnum;
import com.arun.Restaurantbackend.Utilis.OrderType;
import com.arun.Restaurantbackend.Utilis.StatusEnum;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
    private final ItemRepo itemRepo;


    @Value("${api_Url}")
    String api_url;


    @Value("${api_key}")
    String api_key;

    private final UserprofileRepo userprofileRepo;

    private final RestaurantRepo restaurantRepo;
    private final ModelMapper mapper;
    private final OrderRepo orderRepo;
    private final EntityManager entityManager;
    private final Validationhandler validationhandler;
    private final ManagerProfileRepo managerProfileRepo;
    private final DELIVERYASSIGNSERVICE deliveryassignservice;

    private RestClient restClient;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Optional<List<RestaurantDto>> getallbystatus(String status) {
        List<Restaurant> list = restaurantRepo.getallbystatus(status);
        List<RestaurantDto> RestaurantDtolist = list.stream().map(restaurant -> mapper.map(restaurant, RestaurantDto.class)).collect(Collectors.toList());
        return Optional.of(RestaurantDtolist);
    }

    public RestaurantDto findbyname(String name) {
        Restaurant restaurant = restaurantRepo.findByName(name).orElseThrow(() -> new ResourceNoFoundException("No restaurant of this name"));
        return mapper.map(restaurant, RestaurantDto.class);
    }

    @Cacheable(cacheNames = "Restaurant", key = "#id")
    public RestaurantDto findbyid(Long id) {

        Restaurant restaurant = restaurantRepo.findById(id).orElseThrow(() -> new ResourceNoFoundException("No restaurant of this name"));

        log.error("....................." + restaurant);
        return mapper.map(restaurant, RestaurantDto.class);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public RestaurantDto setstatus(Long id, String name) throws InvalidElementException {

        Restaurant restaurant = restaurantRepo.findById(id).orElseThrow(() -> new ResourceNoFoundException("No restaurant found"));

        if (name.equals(String.valueOf(StatusEnum.ACTIVE)) || name.equals(String.valueOf(StatusEnum.PENDING)) || name.equals(String.valueOf(StatusEnum.SUSPENDED))) {
            restaurant.setStatus(name);
        } else {
            throw new BadRequestException("Invalid name");
        }

        return mapper.map(restaurant, RestaurantDto.class);


    }

    @PreAuthorize("hasRole('USER')")
    public List<DemoRestaurant> findbynames(String name) {
        List<Restaurant> list = restaurantRepo.findByNames(name).orElseThrow(() -> new ResourceNoFoundException("No any restaurant"));
        return list.stream().map(item -> mapper.map(item, DemoRestaurant.class)).collect(Collectors.toList());
    }


    public List<DemoRestaurant> findbystatus(String sortBy, Integer pageNumber, Integer size, String active) {

        Optional<List<Restaurant>> restaurantList = restaurantRepo.getallbystatuss(active, PageRequest.of(
                pageNumber,
                size,
                Sort.by(Sort.Order.asc(sortBy), Sort.Order.desc("pinCode"))
        ));

        if (restaurantList.isEmpty()) {
            throw new ResourceNoFoundException("Not found");
        }

        List<DemoRestaurant> list = restaurantList.get().stream().map(item -> mapper.map(item, DemoRestaurant.class)).collect(Collectors.toList());

        return list;

    }

    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    public OrderDto madeorderstaaatuschange(Long restid, Long orderid, OrderEnum orderEnum,
                                            OrderEnum orderEnum2) {
        User user = validationhandler.finduser();
        ManagerProfile managerProfile = managerProfileRepo.findByUserid(user.getId()).orElseThrow(() -> new ResourceNoFoundException("Not found managerprofile"));

        Order order = orderRepo.findByIdAndstatus(orderid, orderEnum2).orElseThrow(() -> new ResourceNoFoundException("Not found exception"));

        if(order.getOrderType().equals(OrderType.BUNDLE) || order.getOrderType().equals(OrderType.GROUP)){
            return new OrderDto();
        }

        if (!(order.getRestaurant().getManagerProfile().getId().equals(managerProfile.getId()))) {
            throw new AccessDeniedException("Not valid manager profile");
        }
        if (!(order.getRestaurant().getId().equals(restid))) {
            throw new AccessDeniedException("not valid resource");
        }
        if ((order.getOrderAcceptTime().isBefore(LocalDateTime.now()) && orderEnum2.equals(OrderEnum.CONFIRMED))) {
            log.error(order.getEstimatedeliverytime() + " ......................");
            throw new ConflictException("Order accept time is expried");
        }
        long minutes = Duration.between(LocalDateTime.now(), order.getCreatedAt()).toMinutes();
        long minute = Duration.between(LocalDateTime.now(), order.getEstimatedeliverytime()).toMinutes();
        if (orderEnum2.equals(OrderEnum.PREPARING)) {
            if (order.getEstimatedeliverytime().isBefore(LocalDateTime.now()) || minutes >= minute) {
                throw new ConflictException("Order accept time is expried");
            }
        }
        order.setStatus(orderEnum);
        order.setEstimatetime(minute + "  min");
        if(orderEnum.equals(OrderEnum.PREPARING)) {
            order.setOrderAcceptTime(LocalDateTime.now().plusMinutes(5));
        }
        else{
            order.setOrderAcceptTime(LocalDateTime.now().plusMinutes(2));
        }
        order.setLastUpdateTime(LocalDateTime.now());
        return mapper.map(order, OrderDto.class);
    }

    public List<ItemDto> getAllmenuItem(Long id) {

        User user = validationhandler.finduser();

        Restaurant restaurant = restaurantRepo.findById(id).orElseThrow(() -> new ResourceNoFoundException("Restaurant is not found"));

        Userprofile userprofile = userprofileRepo.findByUserid(user.getId()).orElseThrow(() -> new ResourceNoFoundException("User Profile is not found"));


        List<ItemDto> itemList = getItemOnbasisOfWeather(restaurant);

        Double km = deliveryassignservice.findkm(userprofile.getSocietyName(), restaurant.getTown());

        List<ItemDto> list = new ArrayList<>();
        itemList.stream()
                .forEach(x -> {
                    if (x.getMax_radius_km() < km) {
                        ItemDto itemDto = mapper.map(x, ItemDto.class);
                        itemDto.setAction(ItemAction.AVAILABLE);
                        list.add(itemDto);
                    } else {
                        ItemDto itemDto = mapper.map(x, ItemDto.class);
                        itemDto.setAction(ItemAction.HIDE);
                        list.add(itemDto);
                    }
                });


        return list;

    }

    public List<ItemDto> getItemOnbasisOfWeather(Restaurant restaurant) {

        restClient = RestClient.builder().baseUrl(api_url).build();


        String town = restaurant.getTown().toLowerCase();
        String city = restaurant.getCity().toLowerCase();
        String locationQuery = town + "," + city;
        JsonNode body = restClient.get()
                .uri(x -> x.path("/current.json")
                        .queryParam("key", api_key)
                        .queryParam("q", locationQuery)
                        .build()
                )
                .retrieve()
                .body(JsonNode.class);


        JsonNode path = body.path("current")
                .path("condition")
                .path("text");

        String weatherText = path.asText().toLowerCase();


        List<Item> list = restaurant.getMenu().getItemList();
        List<ItemDto> list1 = new ArrayList<>();
        if (weatherText.contains("rain") || weatherText.contains("drizzle") || weatherText.contains("shower")
                || weatherText.contains("thunder") || weatherText.contains("storm")) {
            list1.stream()
                    .forEach(item -> {

                        ItemDto itemDto = mapper.map(item, ItemDto.class);

                        Float maxRadiusKm = itemDto.getMax_radius_km();

                        Float value = maxRadiusKm / 4.0f;
                        maxRadiusKm = maxRadiusKm - value;

                        itemDto.setMax_radius_km(maxRadiusKm);
                        list1.add(itemDto);
                    });
        } else {

            list1.stream()
                    .forEach(item -> {

                        ItemDto itemDto = mapper.map(item, ItemDto.class);
                        list1.add(itemDto);
                    });
        }
        return list1;
    }

    @Transactional
    public void changeStatus(Long id, ItemAction itemAction) {

        Item item=itemRepo.findById(id).orElseThrow(()-> new BadRequestException("item is not found"));
        if(!item.getAction().equals(itemAction)){
            item.setAction(itemAction);
        }

    }
}
