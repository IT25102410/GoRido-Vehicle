package com.example.gorido.Repository;
import com.example.gorido.Model.Passengers;
import com.example.gorido.Model.TypeHasPassenger;
import com.example.gorido.Model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TypeHasPassengerRepository extends JpaRepository<TypeHasPassenger, Integer> {
    Optional<TypeHasPassenger> findByVehicleTypeAndPassengersId(
            VehicleType vehicleType,
            Passengers passengersId
    );
}
