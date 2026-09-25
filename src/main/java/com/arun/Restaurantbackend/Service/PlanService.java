package com.arun.Restaurantbackend.Service;


import com.arun.Restaurantbackend.DTO.PlanRequest;
import com.arun.Restaurantbackend.Entity.Plan;
import com.arun.Restaurantbackend.Exception.BadRequestException;
import com.arun.Restaurantbackend.Exception.ConflictException;
import com.arun.Restaurantbackend.Repository.PlanRepository;
import com.fasterxml.jackson.annotation.OptBoolean;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.swing.text.PlainDocument;
import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
private  final ModelMapper mapper;

    @PreAuthorize("hasRole('ADMIN')")
    public Plan addplan(PlanRequest plan){


//        Optional<Plan> plan1=planRepository.findById(plan.getId());

//        if (plan1.isPresent()){
//            throw new ConflictException("Plan id already exist ");
//        }

        return planRepository.save(mapper.map(plan,Plan.class));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public  void removeplan(Long id){
        Optional<Plan> plan1=planRepository.findById(id);

        if (plan1.isEmpty()){
            throw new BadRequestException("Plan id already exist ");
        }

        planRepository.deleteById(id);

    }

}
