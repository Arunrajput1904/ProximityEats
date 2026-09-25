package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.Entity.User;
import com.arun.Restaurantbackend.Entity.Userprofile;
import com.arun.Restaurantbackend.Exception.ResourceAlreadyExistsException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.UserprofileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class UserprofileService {

    private final UserprofileRepo userprofileRepo;

    private final Validationhandler validationhandler;



    @PreAuthorize("hasRole('USER')")
    public Userprofile addaddress(Userprofile userprofile) {

        User user = validationhandler.finduser();
        List<Userprofile> userprofileList = userprofileRepo.findByuserid(user.getId())
                .orElseThrow(() -> new ResourceNoFoundException("Not found"));

        Userprofile finalUserprofile = userprofile;
        userprofileList.stream().forEach(item -> {
            if (item.getCity().equals(finalUserprofile.getCity()) && item.getSocietyName().equals(finalUserprofile.getSocietyName()) && item.getState().equals(finalUserprofile.getState()) && item.getPinCode().equals(item.getPinCode())) {
                throw new ResourceAlreadyExistsException("Resource already exists");
            }
        });
        userprofile.setUserid(user.getId());

        userprofile=userprofileRepo.save(userprofile);

        return userprofile;
    }
}
