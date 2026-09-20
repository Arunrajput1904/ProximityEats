package com.arun.Restaurantbackend.Repository;

import com.arun.Restaurantbackend.Entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.LinkOption;
import java.util.List;

public interface StopRepo extends JpaRepository<Stop, Long> {

    @Query("select e from Stop e where e.bundleId=:id")
    List<Stop> findByBundleId(@PathVariable("id") Long id);
}
