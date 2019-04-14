package ru.rpuch.demo.reactivemongo.web;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.rpuch.demo.reactivemongo.domain.Vehicle;
import ru.rpuch.demo.reactivemongo.domain.Speed;
import ru.rpuch.demo.reactivemongo.repository.VehicleRepository;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * @author rpuch
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VehicleRouterTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private VehicleRepository repository;

    private Vehicle vehicle = new Vehicle("id1", "automatic", "enabled", new Speed("mps", 110.12121212));
    private Vehicle secondVehicle = new Vehicle("id2", "manual", "disabled", new Speed("kph", 1000));

    @After
    public void cleanup() {
        repository.deleteAll().block();
    }

    @Test
    public void givenCollectionIsEmpty_whenAVehicleIsPosted_thenItShouldBeCreatedAndReturned() {
        Vehicle returnedVehicle = webTestClient
                .post().uri("/vehicle")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(
                        TestResources.resourceAsString("vehicle-to-create.json", getClass())
                ))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Vehicle.class).value(this::verifyCreatedVehicle)
                .returnResult()
                .getResponseBody();

        Vehicle loadedVehicle = repository.findById(returnedVehicle.getId()).block();
        assertThat(loadedVehicle, is(notNullValue()));
        verifyCreatedVehicle(loadedVehicle);
    }

    private void verifyCreatedVehicle(Vehicle vehicleToVerify) {
        assertThat(vehicleToVerify.getId(), is(notNullValue()));
        assertThat(vehicleToVerify.getCode(), is("id1"));
        assertThat(vehicleToVerify.getTransmission(), is("automatic"));
        assertThat(vehicleToVerify.getAi(), is("enabled"));
        assertThat(vehicleToVerify.getMaxSpeed().getUnit(), is("mps"));
        assertThat(vehicleToVerify.getMaxSpeed().getValue(), is(110.12121212));
    }

    private void verifyVehicles(List<Vehicle> vehicles, int expectedCount) {
        assertThat(vehicles, hasSize(expectedCount));
    }

    @Test
    public void givenAVehicleExists_whenAVehicleIsRetrieved_thenItShouldBeReturned() {
        repository.save(vehicle).block();

        webTestClient
                .get().uri("/vehicle/" + vehicle.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vehicle.class).value(this::verifyCreatedVehicle);
    }

    @Test
    public void givenNoVehicleExists_whenAVehicleIsRetrieved_then404ShouldBeReturned() {
        webTestClient
                .get().uri("/vehicle/no-such-vehicle")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void givenAVehicleExists_whenAVehicleIsPut_thenItShouldBeUpdatedAndReturned() {
        repository.save(vehicle).block();

        webTestClient
                .put().uri("/vehicle/" + vehicle.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(
                        TestResources.resourceAsString("vehicle-to-update.json", getClass())
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vehicle.class).value(this::verifyUpdatedVehicle);

        Vehicle loadedVehicle = repository.findById(vehicle.getId()).block();
        assertThat(loadedVehicle, is(notNullValue()));
        verifyUpdatedVehicle(loadedVehicle);
    }

    @Test
    public void givenNoVehicleExists_whenAVehicleIsPut_then404ShouldBeReturned() {
        webTestClient
                .put().uri("/vehicle/no-such-vehicle")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(
                        TestResources.resourceAsString("vehicle-to-update.json", getClass())
                ))
                .exchange()
                .expectStatus().isNotFound();
    }

    private void verifyUpdatedVehicle(Vehicle vehicleToVerify) {
        assertThat(vehicleToVerify.getCode(), is("id2"));
        assertThat(vehicleToVerify.getTransmission(), is("manual"));
        assertThat(vehicleToVerify.getAi(), is("disabled"));
        assertThat(vehicleToVerify.getMaxSpeed().getUnit(), is("kph"));
        assertThat(vehicleToVerify.getMaxSpeed().getValue(), is(1000.0));
    }

    @Test
    public void givenAVehicleExists_whenAVehicleIsDeleted_thenItShouldBeDeleted() {
        repository.save(vehicle).block();

        webTestClient
                .delete().uri("/vehicle/" + vehicle.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

        Vehicle loadedVehicle = repository.findById(vehicle.getId()).block();
        assertThat(loadedVehicle, is(nullValue()));
    }

    @Test
    public void givenNoVehicleExists_whenAVehicleIsDeleted_then404ShouldBeReturned() {
        webTestClient
                .delete().uri("/vehicle/no-such-vehicle")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void given2VehiclesExist_whenVehiclesAreListed_then2VehiclesAreReturned() {
        repository.save(vehicle).block();
        repository.save(secondVehicle).block();

        webTestClient
                .get().uri("/vehicle")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Vehicle>>() {})
                .value(vehicles -> verifyVehicles(vehicles, 2));
    }

    @Test
    public void given2VehiclesExist_whenVehiclesAreSearchByCode_then1VehicleIsReturned() {
        repository.save(vehicle).block();
        repository.save(secondVehicle).block();

        webTestClient
                .get().uri("/vehicle?field=code&value=id1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Vehicle>>() {})
                .value(vehicles -> verifyVehicles(vehicles, 1));
    }
}