package com.example.gorido.Repository;
import com.example.gorido.Model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByNumberAndStatusId_Id(String number, Integer statusId);
    List<Vehicle> findByDriverId(Driver driverId);
}
