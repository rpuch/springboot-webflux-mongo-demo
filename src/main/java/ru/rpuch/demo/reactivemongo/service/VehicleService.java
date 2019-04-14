package ru.rpuch.demo.reactivemongo.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpuch.demo.reactivemongo.domain.Vehicle;

/**
 * @author rpuch
 */
public interface VehicleService {
    Mono<Vehicle> createVehicle(Vehicle vehicle);

    Mono<Vehicle> retrieveVehicle(String vehicleId);

    Mono<Vehicle> updateVehicle(String vehicleId, Vehicle newVehicle);

    Mono<DeletionStatus> deleteVehicle(String vehicleId);

    Flux<Vehicle> listVehicles();

    Flux<Vehicle> searchVehicles(FieldExample example);
}
