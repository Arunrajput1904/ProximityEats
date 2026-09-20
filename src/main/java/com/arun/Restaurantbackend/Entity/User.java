package com.arun.Restaurantbackend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Builder
@Table(name = "User_Info", schema = "user_service")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank(message = "User name should not be black or empty")
    private String name;


    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

    @Email
    @NotEmpty
    String email;


    @Size(min = 8, message = "Pasword should ne atleast 8 length")
    String password;


    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Wallet wallet;




    private Long cancelApproveTime;


    private Boolean profileComplete=false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> list = new ArrayList<>();
            list.add(new SimpleGrantedAuthority("ROLE_" + role.getType()));

        return list;
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }


}


