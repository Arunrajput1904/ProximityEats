package com.arun.Restaurantbackend.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocietyEdgeDto {


    Long id;


    SocietyDto society1;

    SocietyDto society2;


    Double distance;




}
