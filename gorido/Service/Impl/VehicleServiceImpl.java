package com.example.gorido.Service.Impl;

import com.example.gorido.DTO.VehicleRegisterRequest;
import com.example.gorido.Model.*;
import com.example.gorido.Repository.*;
import com.example.gorido.Service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
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
    //private final UserRepository userRepository;
    //private final DriverRepository driverRepository;
    private final StatusRepository statusRepository;
    private final PassengerRepository passengerRepository;
    private final TypeHasPassengerRepository typeHasPassengerRepository;

    public VehicleServiceImpl(VehicleTypeRepository vehicleTypeRepository,
                              VehicleColorRepository vehicleColorRepository, VehicleBrandRepository vehicleBrandRepository,
                              TypeHasBrandRepository typeHasBrandRepository, VehicleRepository vehicleRepository,
                              //UserRepository userRepository, DriverRepository driverRepository,
                              StatusRepository statusRepository, TypeHasPassengerRepository typeHasPassengerRepository,
                              PassengerRepository passengerRepository){
        this.vehicleBrandRepository = vehicleBrandRepository;
        this.vehicleColorRepository = vehicleColorRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.typeHasBrandRepository = typeHasBrandRepository;
        this.vehicleRepository = vehicleRepository;
        //this.userRepository = userRepository;
        //this.driverRepository = driverRepository;
        this.statusRepository = statusRepository;
        this.typeHasPassengerRepository = typeHasPassengerRepository;
        this.passengerRepository = passengerRepository;
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

    public String loadPassengers(int typeId) {

        List<Passengers> list = passengerRepository.findPassengersByTypeId(typeId);

        StringBuilder sb = new StringBuilder();

        for (Passengers p : list) {
            sb.append(p.getId())
                    .append(":")
                    .append(p.getNumber())
                    .append(",");
        }

        return sb.toString();
    }

    public String addVehicle(VehicleRegisterRequest request, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        //User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        if (user.getTypeId().getId() != 2) {
            return "First create driver account";
        }

        //Driver driver = driverRepository.findByUserId(user).orElse(null);

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

        Passengers passenger = passengerRepository.findById(request.getPassengersID()).orElse(null);

        if (passenger == null) {
            return "error: Passenger not found";
        }

        Optional<TypeHasPassenger> existingPassenger = typeHasPassengerRepository.findByVehicleTypeAndPassengersId(type, passenger);

        TypeHasPassenger typeHasPassenger;

        if (existingPassenger.isEmpty()) {
            return "Database Error, Try Again Later";
        }

        typeHasPassenger = existingPassenger.get();

        vehicle.setVehicleTypeHasBrand(typeHasBrand);
        vehicle.setTypeHasPassenger(typeHasPassenger);
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

        String basePath = "J:/New folder/GoRido/images/";
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

}
