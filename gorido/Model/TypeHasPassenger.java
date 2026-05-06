package com.example.gorido.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "type_has_passengers")
public class TypeHasPassenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @ManyToOne
    @JoinColumn(name = "passengers_id")
    private Passengers passengersId;

    public TypeHasPassenger() {}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setPassengersId(Passengers passengersId) {
        this.passengersId = passengersId;
    }

    public Passengers getPassengersId() {
        return passengersId;
    }
}
