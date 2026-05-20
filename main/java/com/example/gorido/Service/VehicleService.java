package com.example.gorido.Service;
import com.example.gorido.DTO.VehicleRegisterRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

public interface VehicleService {
    String loadOptions();
    String loadBrands(int typeId);
    String addVehicle(VehicleRegisterRequest request, HttpSession session);
    String manageVehicle(Model model, HttpSession session);
    String delete(HttpSession session, String number);
    String changeStatus(HttpSession session, String number);
}
