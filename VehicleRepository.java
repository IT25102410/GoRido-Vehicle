package com.example.gorido.Repository;
import com.example.gorido.Model.Driver;
import com.example.gorido.Model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByNumberAndStatusId_Id(String number, Integer statusId);
    List<Vehicle> findByDriverId(Driver driverId);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.driverId.id = :driverId AND v.statusId.id = :statusId")
    int countActiveVehiclesByDriverId(@Param("driverId") int driverId,
                                      @Param("statusId") int statusId);
}
