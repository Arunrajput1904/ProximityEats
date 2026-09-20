package com.arun.Restaurantbackend.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Estimatetimekm {

    Double estkm;

    LocalDateTime estimatetime;

    StringBuilder stringBuilder;
}
