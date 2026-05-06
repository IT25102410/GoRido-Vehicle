package com.example.gorido.Repository;
import com.example.gorido.Model.TypeHasBrand;
import com.example.gorido.Model.VehicleBrand;
import com.example.gorido.Model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeHasBrandRepository extends JpaRepository<TypeHasBrand, Integer> {
    Optional<TypeHasBrand> findByVehicleBrandAndVehicleType(
            VehicleBrand vehicleBrand,
            VehicleType vehicleType
    );
}
