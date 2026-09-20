package com.arun.Restaurantbackend.DTO;
import com.arun.Restaurantbackend.Entity.Order;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderBundleDto {

    Long id;



    Order order;

    private Integer positionInRoute;


    BundleDto bundle;


}
