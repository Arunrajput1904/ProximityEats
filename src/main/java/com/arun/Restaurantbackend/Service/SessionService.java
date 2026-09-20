package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.Entity.SessionEntity;
import com.arun.Restaurantbackend.Entity.User;

import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Exception.SessionAuthenticationException;
import com.arun.Restaurantbackend.Repository.SessionRepo;
import com.arun.Restaurantbackend.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final SessionRepo sessionRepo ;
    private  final JwtService jwtcreation;
    private final UserRepo userRepo;
private  final  int limit=1;
    public  void sessioncreation(User user, String token2) {
        List<SessionEntity>list=sessionRepo.findAllByuserId(user.getId());

        Comparator<SessionEntity> comparator=new Comparator<SessionEntity>() {
            @Override
            public int compare(SessionEntity o1, SessionEntity o2) {
                if(o1.getLocalDateTime().isAfter(o2.getLocalDateTime())){
                    return 1;
                }
                else if(o1.getLocalDateTime().isBefore(o2.getLocalDateTime())){
                    return -1;
                }
                else{
                    return  0;
                }
            }
        };

        if(list.size()>=limit){
            list.sort(comparator);
            sessionRepo.delete(list.get(0));
        }

        SessionEntity sessionEntity=SessionEntity.builder().refreshToken(token2).user(user).build();

        sessionRepo.save(sessionEntity);

    }

    public Optional<String> accessTokencreation(String refreshtoken){
        SessionEntity sessionEntity=sessionRepo.findByRefreshToken(refreshtoken).orElseThrow(()-> new SessionAuthenticationException("There is no any session found"));


        Long id=jwtcreation.getIdByJwtToken(refreshtoken);

        Optional<User> user= userRepo.findById(id);
        if (user.isEmpty()){
            throw new ResourceNoFoundException("User is not exist in db");
        }
        String accesstoken=jwtcreation.jwtAccesstoken(user.get());

       return Optional.of(accesstoken);
    }

    @Transactional
    public  void Deleterefreshtoken(String refreshToken){
        sessionRepo.deleteByRefreshToken(refreshToken);
    }


    @Transactional
    public void deleteallsession(User user1) {
        sessionRepo.deleteAllByUser(user1);
    }


}
