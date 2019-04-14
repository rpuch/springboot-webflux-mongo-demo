package ru.rpuch.demo.reactivemongo.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.rpuch.demo.reactivemongo.domain.Vehicle;
import ru.rpuch.demo.reactivemongo.domain.Speed;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author rpuch
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class VehicleRepositoryTest {
    @Autowired
    private VehicleRepository repository;

    private Vehicle vehicle = new Vehicle("abc", "manual", "enabled", new Speed("kph", 200.0));
    private Vehicle secondVehicle = new Vehicle("def", "automatic", "disabled", new Speed("mph", 150.0));

    @After
    public void cleanup() {
        repository.deleteAll().block();
    }

    @Test
    public void givenVehicleIsCreated_whenFindAll_thenFindTheVehicle() {
        saveTheVehicle();

        Flux<Vehicle> vehicleFlux = repository.findAll();

        StepVerifier
                .create(vehicleFlux)
                .assertNext(this::verifyLoadedVehicle)
                .expectComplete()
                .verify();
    }

    private void saveTheVehicle() {
        repository.save(vehicle).block();
    }

    private void verifyLoadedVehicle(Vehicle loadedVehicle) {
        assertNotNull(loadedVehicle.getId());
        assertThat(loadedVehicle.getCode(), is("abc"));
        assertThat(loadedVehicle.getTransmission(), is("manual"));
        assertThat(loadedVehicle.getAi(), is("enabled"));
        assertThat(loadedVehicle.getMaxSpeed().getUnit(), is("kph"));
        assertThat(loadedVehicle.getMaxSpeed().getValue(), is(200.0));
    }

    @Test
    public void givenVehicleIsCreated_whenRetrieve_thenFindTheVehicle() {
        saveTheVehicle();

        Mono<Vehicle> vehicleMono = repository.findById(vehicle.getId());

        StepVerifier
                .create(vehicleMono)
                .assertNext(this::verifyLoadedVehicle)
                .expectComplete()
                .verify();
    }

    @Test
    public void givenVehicleIsCreated_whenUpdate_thenVehicleIsUpdated() {
        saveTheVehicle();

        vehicle.setMaxSpeed(new Speed("kph", 1000.0));
        repository.save(vehicle).block();

        Mono<Vehicle> vehicleMono = repository.findById(vehicle.getId());
        StepVerifier
                .create(vehicleMono)
                .assertNext(loadedVehicle -> {
                    assertThat(loadedVehicle.getMaxSpeed().getUnit(), is("kph"));
                    assertThat(loadedVehicle.getMaxSpeed().getValue(), is(1000.0));
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void givenVehicleIsCreated_whenDelete_thenVehicleIsNotFoundAnymore() {
        saveTheVehicle();

        repository.deleteById(vehicle.getId()).block();

        Vehicle loadedVehicle = repository.findById(vehicle.getId()).block();
        assertThat(loadedVehicle, is(nullValue()));
    }

    @Test
    public void given2VehiclesAreCreated_whenFindAll_thenFindTheVehicles() {
        saveTwoTracks();

        Flux<Vehicle> vehicleFlux = repository.findAll();

        AtomicInteger count = new AtomicInteger(0);
        StepVerifier
                .create(vehicleFlux)
                .thenConsumeWhile(vehicle -> {
                    count.incrementAndGet();
                    return true;
                })
                .expectComplete()
                .verify();
        assertThat(count.get(), is(2));
    }

    private void saveTwoTracks() {
        saveTheVehicle();
        repository.save(secondVehicle).block();
    }

    @Test
    public void given2VehiclesAreCreated_whenFindByFirstCode_thenFirstVehicleShouldBeFound() {
        saveTwoTracks();

        Flux<Vehicle> vehicleFlux = repository.findAllByDynamicField("code", "abc");

        AtomicInteger count = new AtomicInteger(0);
        StepVerifier
                .create(vehicleFlux)
                .thenConsumeWhile(vehicle -> {
                    verifyLoadedVehicle(vehicle);
                    count.incrementAndGet();
                    return true;
                })
                .expectComplete()
                .verify();
        assertThat(count.get(), is(1));
    }
}