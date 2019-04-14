package ru.rpuch.demo.reactivemongo.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpuch.demo.reactivemongo.domain.Vehicle;
import ru.rpuch.demo.reactivemongo.repository.VehicleRepository;

/**
 * @author rpuch
 */
@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final DeleteOperation<Vehicle, String> deleteOperation;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
        deleteOperation = new DeleteOperation<>(vehicleRepository);
    }

    @Override
    public Mono<Vehicle> createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Mono<Vehicle> retrieveVehicle(String vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    @Override
    public Mono<Vehicle> updateVehicle(String vehicleId, Vehicle newVehicle) {
        return Mono.just(newVehicle)
                .filterWhen(vehicle -> vehicleRepository.existsById(vehicleId))
                .flatMap(vehicle -> {
                    vehicle.setId(vehicleId);
                    return vehicleRepository.save(vehicle);
                });
    }

    @Override
    public Mono<DeletionStatus> deleteVehicle(String vehicleId) {
        return deleteOperation.delete(vehicleId);
    }

    @Override
    public Flux<Vehicle> listVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public Flux<Vehicle> searchVehicles(FieldExample example) {
        return vehicleRepository.findAllByDynamicField(example.getField(), example.getValue());
    }
}
