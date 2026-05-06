package com.example.gorido.Repository;
import com.example.gorido.Model.Passengers;
import com.example.gorido.Model.VehicleBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passengers, Integer> {
    @Query("SELECT tp.passengersId FROM TypeHasPassenger tp WHERE tp.vehicleType.id = :typeId")
    List<Passengers> findPassengersByTypeId(@Param("typeId") int typeId);
}
