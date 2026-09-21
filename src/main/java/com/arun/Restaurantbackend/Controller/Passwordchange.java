package com.arun.Restaurantbackend.Controller;

import com.arun.Restaurantbackend.DTO.Emailinput;
import com.arun.Restaurantbackend.DTO.changepassword;
import com.arun.Restaurantbackend.Service.AuthService;
import com.arun.Restaurantbackend.Service.PasswordchangeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-password")
@Tag(name = "2. Password Apis", description = "Give Password change")
public class Passwordchange {

    private final AuthService authService;

private final PasswordchangeService passwordchangeService;

    @PutMapping("/change-password")
    public ResponseEntity<String> makepasswordchange(@RequestBody  @Valid  changepassword user) {

        Boolean bool=passwordchangeService.changepassword(user);
        if(bool.equals(false)){

            throw new BadCredentialsException("Invalid creaditails");

        }

        return ResponseEntity.ok("changed succesfully");

    }

    @PostMapping("/sendEmail")
    public  ResponseEntity<?> sendotp(@RequestBody  @Valid Emailinput emailinput){
        passwordchangeService.sendemailotp(emailinput.getEmail());
        return ResponseEntity.ok("Send successfully");
    }

}
