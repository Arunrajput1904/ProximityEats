package com.arun.Restaurantbackend.DTO;


import com.arun.Restaurantbackend.Entity.Order;
import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Utilis.EmailType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class EmailEvent {

    private EmailType type;

    private String to;

    private String message;

    private Order order;

    private User user;

    public EmailEvent() {
    }

    // constructors, getters, setters
}
