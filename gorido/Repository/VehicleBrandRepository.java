package com.example.gorido.Repository;
import com.example.gorido.Model.VehicleBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VehicleBrandRepository extends JpaRepository<VehicleBrand, Integer> {
    @Query("SELECT thb.vehicleBrand FROM TypeHasBrand thb WHERE thb.vehicleType.id = :typeId")
    List<VehicleBrand> findBrandsByTypeId(@Param("typeId") int typeId);
}