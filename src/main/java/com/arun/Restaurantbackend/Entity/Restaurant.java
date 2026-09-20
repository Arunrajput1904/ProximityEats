package com.arun.Restaurantbackend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(schema = "restaurant_service")
public class Restaurant {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "User name should not be black or empty")
    private String name;


    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

    @NotEmpty
    String town;

    @NotEmpty
    String city;

    @NotEmpty
    String state;

    @NotEmpty
    String pinCode;

    @JsonIgnore
    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private Menu menu;
    @NotNull
    private String imageUrl;



    @NotEmpty
    private String status;

    @NotEmpty
    @Email
    private String email;


    private Long cancelApproveTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "manager_profile_id", nullable = false)
    private ManagerProfile managerProfile;

}
