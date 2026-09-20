package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Society;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocietyRepo extends JpaRepository<Society,Long> {

    @Query("SELECT s FROM Society s WHERE s.societyName = :societyName AND s.pinCode = :pinCode")
    Optional<Society> findByNameAndPincode(@Param("societyName") String societyName,
                                           @Param("pinCode") String pinCode);

    @Query("select e from Society e  where e.societyName=:societyName")
    Optional<Society> findByName(@NotBlank(message = "HomeTown should not be black or empty") @Size(max = 100,min = 3,message = "Hometown name should be of size 3 to 10") String societyName);
}
