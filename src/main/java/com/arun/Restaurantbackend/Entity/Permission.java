package com.arun.Restaurantbackend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "user_service")
public class Permission {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Title is not empty/black")
    String title;


    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(mappedBy = "permissionList")
    List<Role> roleList = new ArrayList<>();

}
