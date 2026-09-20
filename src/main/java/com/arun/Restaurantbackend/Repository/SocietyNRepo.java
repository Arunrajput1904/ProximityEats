package com.arun.Restaurantbackend.Repository;


import com.arun.Restaurantbackend.Entity.SocietyN;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SocietyNRepo extends JpaRepository<SocietyN,Long> {

    @Query("select e from SocietyN e where e.SocietyName=:SocietyName")
    Optional<SocietyN> findBySocietyName(@Param("SocietyName") String cityname);


}
