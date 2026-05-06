package com.example.gorido.Service;

import com.example.gorido.DTO.VehicleRegisterRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

public interface VehicleService {
    String loadOptions();
    String loadBrands(@RequestParam int typeId);
    String loadPassengers(@RequestParam int typeId);
    String addVehicle(VehicleRegisterRequest request, HttpSession session);
}
