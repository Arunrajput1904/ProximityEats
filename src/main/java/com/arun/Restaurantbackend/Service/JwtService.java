package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Exception.InsufficientAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {


    @Value("${secretValue}")
     String roughtoken;

    SecretKey getString(){
        return Keys.hmacShaKeyFor(roughtoken.getBytes(StandardCharsets.UTF_8));
    }


   public String  jwtAccesstoken(User user){

        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*10))
                .subject(user.getId().toString())
                .claim("PhoneNumber",user.getPhoneNumber())
                .claim("name",user.getName())
                .claim("role",user.getRole())
                .signWith(getString())
                .compact();
    }
   public String  jwtRefreshtoken(User user){
try{

    return Jwts.builder()
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis()+1000*60*20))
            .subject(user.getId().toString())
            .signWith(getString())
            .compact();
}
catch (JwtException ex){
    throw new InsufficientAuthenticationException("Invalid jwt exception");
}
    }


   public Long  getIdByJwtToken(String token){
        return Long.valueOf(Jwts.parser().verifyWith(getString()).build().parseSignedClaims(token).getPayload().getSubject());
    }


   public  Boolean validtoken(String roughtoken){
        Date date=Jwts.parser().verifyWith(getString()).build().parseSignedClaims(roughtoken).getPayload().getExpiration();

        return date.after(new Date());
   }



}
