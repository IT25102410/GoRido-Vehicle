package com.example.gorido.Service.Impl;

import com.example.gorido.DTO.VehicleRegisterRequest;
import com.example.gorido.Model.*;
import com.example.gorido.Repository.*;
import com.example.gorido.Service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleColorRepository vehicleColorRepository;
    private final VehicleBrandRepository vehicleBrandRepository;
    private final TypeHasBrandRepository typeHasBrandRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final StatusRepository statusRepository;

    public VehicleServiceImpl(VehicleTypeRepository vehicleTypeRepository,
                              VehicleColorRepository vehicleColorRepository, VehicleBrandRepository vehicleBrandRepository,
                              TypeHasBrandRepository typeHasBrandRepository, VehicleRepository vehicleRepository, UserRepository userRepository,
                              DriverRepository driverRepository, StatusRepository statusRepository){
        this.vehicleBrandRepository = vehicleBrandRepository;
        this.vehicleColorRepository = vehicleColorRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.typeHasBrandRepository = typeHasBrandRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.statusRepository = statusRepository;
    }

    public String loadOptions(){
        List<VehicleType> vehicleType = vehicleTypeRepository.findAll();
        List<VehicleColor> vehicleColor = vehicleColorRepository.findAll();

        StringBuilder typeStr = new StringBuilder();
        for (VehicleType t : vehicleType){
            typeStr.append(t.getId()).append(":").append(t.getName()).append(",");
        }

        StringBuilder colorStr = new StringBuilder();
        for (VehicleColor c : vehicleColor){
            colorStr.append(c.getId()).append(":").append(c.getName()).append(",");
        }

        return typeStr.toString() + "|" + colorStr.toString();
    }

    public String loadBrands(@RequestParam int typeId) {

        List<VehicleBrand> brands = vehicleBrandRepository.findBrandsByTypeId(typeId);

        StringBuilder brandStr = new StringBuilder();

        for (VehicleBrand b : brands) {
            brandStr.append(b.getId())
                    .append(":")
                    .append(b.getName())
                    .append(",");
        }

        return brandStr.toString();
    }

    public String addVehicle(VehicleRegisterRequest request, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        if (user.getTypeId().getId() != 2) {
            return "First create driver account";
        }

        Driver driver = driverRepository.findByUserId(user).orElse(null);

        if (driver == null) {
            return "Driver not found";
        }

        Optional<Vehicle> activevehicle = vehicleRepository.findByNumberAndStatusId_Id(request.getNumber(), 1);

        if (activevehicle.isPresent()) {
            return "This vehicle is already registered";
        }

        Optional<Status> status = statusRepository.findById(1);
        if (status.isEmpty()) {
            return "error: Status not found";
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setDriverId(driver);
        vehicle.setModel(request.getModel());
        vehicle.setNumber(request.getNumber());
        vehicle.setInsurance_number(request.getInsurance_number());
        vehicle.setInsurance_exp_date(request.getInsurance_exp_date());
        vehicle.setStatusId(status.get());

        int driverId = driver.getId();

        VehicleType type = vehicleTypeRepository.findById(request.getTypeId()).orElse(null);
        if (type == null) {
            return "error: Type not found";
        }

        VehicleBrand brand = vehicleBrandRepository.findById(request.getBrandId()).orElse(null);
        if (brand == null) {
            return "error: Brand not found";
        }

        Optional<TypeHasBrand> existing = typeHasBrandRepository.findByVehicleBrandAndVehicleType(brand, type);

        TypeHasBrand typeHasBrand;

        if (existing.isEmpty()) {
            return "Database Error, Try Again Later";
        }
        typeHasBrand = existing.get();

        vehicle.setTypeHasBrand(typeHasBrand);
        VehicleColor color = vehicleColorRepository.findById(request.getColor()).orElse(null);
        if (color == null){
            return "error: Color not found";
        }

        if (driver.getStatusId() != null && driver.getStatusId().getId() == 2) {

            Optional<Status> activeStatus = statusRepository.findById(1);

            if (activeStatus.isEmpty()) {
                return "error: Status not found";
            }

            driver.setStatusId(activeStatus.get());
            driverRepository.save(driver);
        }

        vehicle.setVehicleColor(color);
        vehicleRepository.save(vehicle);

        int vehicleId = vehicle.getId();

        String basePath = "C:/Users/HP/Downloads/GoRido/src/images";
        String isuarancePath;
        String bookPath;
        String vehiclePath;

        try {
            isuarancePath = saveFile(request.getInsuranceImage(), basePath + "driver/vehicle/isuarance", driverId + "_" + vehicleId + "_isuarance.jpg");
            bookPath = saveFile(request.getBookImage(), basePath + "driver/vehicle/book", driverId + "_" + vehicleId + "_book.jpg");
            vehiclePath = saveFile(request.getVehicleImage(), basePath + "driver/vehicle/vehicle_images", driverId + "_" + vehicleId + "_vehicle.jpg");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }

        vehicle.setInsurance_photo(isuarancePath);
        vehicle.setVehicle_book(bookPath);
        vehicle.setVehicle_photo(vehiclePath);
        vehicleRepository.save(vehicle);

        return "success";
    }

    private String saveFile(MultipartFile file, String folderPath, String fileName) throws IOException {

        File dir = new File(folderPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File saveFile = new File(dir, fileName);
        file.transferTo(saveFile);

        return fileName;
    }

    public String manageVehicle(Model model, HttpSession session){
        String email = (String) session.getAttribute("userEmail");

        if (email == null) {
            return "redirect:/signin";
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/signin";
        }

        Driver driver = driverRepository.findByUserId(user).orElse(null);
        if (driver == null){
            return "redirect:/driverregi";
        }

        List<Vehicle> vehicles = vehicleRepository.findByDriverId(driver);
        model.addAttribute("vehicles", vehicles);

        return "managevehicle";
    }

    public String delete(HttpSession session, String number){

        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "Session expired";

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return "User not found";

        Driver driver = user.getDriver();
        if (driver == null) return "Driver not found";

        Vehicle matchedVehicle = null;
        List<Vehicle> vehicleList = driver.getVehicles();

        if (vehicleList.size() <= 1) {
            Optional<Status> deactiveStatus = statusRepository.findById(2);

            if (deactiveStatus.isEmpty()) {
                return "error: Status not found";
            }

            driver.setStatusId(deactiveStatus.get());
            driverRepository.save(driver);
        }

        for (Vehicle v : vehicleList) {
            if (number.equals(v.getNumber())) {
                matchedVehicle = v;
                break;
            }
        }

        if (matchedVehicle == null) {
            return "Vehicle not found";
        }

        driver.getVehicles().remove(matchedVehicle);
        matchedVehicle.setDriverId(null);

        deleteFile(matchedVehicle.getInsurance_photo(), "driver/vehicle/isuarance");
        deleteFile(matchedVehicle.getVehicle_book(), "driver/vehicle/book");
        deleteFile(matchedVehicle.getVehicle_photo(), "driver/vehicle/vehicle_images");

        vehicleRepository.delete(matchedVehicle);

        return "success";
    }

    private void deleteFile(String fileName, String folder) {

        if (fileName == null) return;

        String basePath = "C:/Users/HP/Downloads/GoRido/src/images";

        File file = new File(basePath + folder + "/" + fileName);

        if (file.exists()) {
            file.delete();
        }
    }

    public String changeStatus(HttpSession session, String number){

        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "Session expired";

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return "User not found";

        Driver driver = user.getDriver();
        if (driver == null) return "Driver not found";

        Vehicle matchedVehicle = null;
        List<Vehicle> vehicleList = driver.getVehicles();

        for (Vehicle v : vehicleList) {
            if (number.equals(v.getNumber())) {
                matchedVehicle = v;
                break;
            }
        }

        if (matchedVehicle == null) {
            return "Vehicle not found";
        }

        Optional<Status> activeStatus = statusRepository.findById(1);
        Optional<Status> deactiveStatus = statusRepository.findById(2);

        if (activeStatus.isEmpty() || deactiveStatus.isEmpty()) {
            return "error: Status not found";
        }

        int status = matchedVehicle.getStatusId().getId();

        if (status == 1){
            matchedVehicle.setStatusId(deactiveStatus.get());
        } else if (status == 2){
            matchedVehicle.setStatusId(activeStatus.get());
        }

        vehicleRepository.save(matchedVehicle);

        return "success";
    }

}
