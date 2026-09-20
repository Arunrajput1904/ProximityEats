package com.arun.Restaurantbackend.Controller;
import com.arun.Restaurantbackend.Entity.Item;
import com.arun.Restaurantbackend.Entity.Menu;
import com.arun.Restaurantbackend.Repository.MenuRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class MenucontrollerTestIT {
Menu menu;
Item item;

    @Autowired
    MenuRepo menuRepo;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setMenu(){
         menu=menuRepo.findById(1l).orElse(null);
        item=Item.builder().name("cake").price(120.9).build();
    }


    @Test
    void Post_menu_item_success(){
      webTestClient
              .post()
              .uri("/api/menu/additem/{id}")
              .bodyValue(item)
              .exchange()
              .expectBody(Menu.class)
              .value(v-> {assertThat(v.getRestaurant().getName()).isEqualTo(menu.getRestaurant().getName()) ;
                  assertThat(v.getId()).isEqualTo(menu.getId());});
    }

}