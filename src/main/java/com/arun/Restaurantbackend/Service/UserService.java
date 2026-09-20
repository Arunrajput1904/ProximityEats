package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {


    final  private UserRepo userRepo;



    @Override
    public UserDetails  loadUserByUsername(String username)  {
    UserDetails userDetails=null;
        try{
        userDetails=userRepo.findByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("Username is not found in db"));
        if(userDetails.getPassword()==null){
            throw new UsernameNotFoundException("Please login with Oauth2authication");
        }
    }
    catch (BadCredentialsException exception){
        throw new UsernameNotFoundException(exception.getMessage());
    }

return userDetails;
    }

    public User savess(User user){
        return userRepo.save(user);
    }
}
