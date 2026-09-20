package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.DTO.Testing;
import com.arun.Restaurantbackend.Entity.SocietyEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SocietyEdgeRepo extends JpaRepository<SocietyEdge,Long> {

    @Query("select e from SocietyEdge e")
    List<SocietyEdge> findAllSocietyEdge();

    @Query("select new  com.arun.Restaurantbackend.DTO.Testing( e.distance , count(e) , sum(e.distance) as sum ) from SocietyEdge e Group By e.distance Order By sum")
    List<Testing> findbyquery();
}
