package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Emailvalidation;
//import com.arun.Restaurantbackend.Entity.Emailvalidations;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface EmailvalidationRepo extends JpaRepository<Emailvalidation,Long> {
  

    @Transactional
    void deleteAllByEmail(String to);

    Optional<Emailvalidation> findByEmail(@Email @NotEmpty String email);
}
