package com.arun.Restaurantbackend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ToString
@Table(schema = "user_service")
public class Userprofile{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "City should not be black or empty")
    @Size(max = 100,min = 3,message = "City name should be of size 3 to 10")
    private String city;


    @NotBlank(message = "HomeTown should not be black or empty")
    @Size(max = 100,min = 3,message = "Hometown name should be of size 3 to 10")
    private String SocietyName;

    @NotNull
    Long userid;


@NotEmpty
    String state;

@NotEmpty
    String pinCode;



}


