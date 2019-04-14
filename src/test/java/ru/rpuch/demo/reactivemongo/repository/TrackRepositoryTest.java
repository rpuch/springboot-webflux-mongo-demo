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
import ru.rpuch.demo.reactivemongo.domain.Length;
import ru.rpuch.demo.reactivemongo.domain.Speed;
import ru.rpuch.demo.reactivemongo.domain.Track;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author rpuch
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class TrackRepositoryTest {
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private TrackRepository trackRepository;

    private Track track = new Track("abc", "a long description", new Length("km", 7.4),
            Arrays.asList(
                    new Vehicle("abc", "manual", "enabled", new Speed("kph", 200.0)),
                    new Vehicle("def", "automatic", "disabled", new Speed("mph", 150.0))
            ));
    private Track secondTrack = new Track("def", "a short description", new Length("ft", 500.0),
            Collections.emptyList());

    @After
    public void cleanup() {
        trackRepository.deleteAll()
                .then(vehicleRepository.deleteAll())
                .then().block();
    }

    @Test
    public void givenTrackIsCreated_whenFindAll_thenFindTheTrack() {
        saveTheTrack();

        Flux<Track> trackFlux = trackRepository.findAll();

        StepVerifier
                .create(trackFlux)
                .assertNext(this::verifyLoadedTrack)
                .expectComplete()
                .verify();
    }

    private void saveTheTrack() {
        Vehicle vehicle1 = vehicleRepository.save(new Vehicle("abc", "manual", "enabled", new Speed("kph", 200.0))).block();
        Vehicle vehicle2 = vehicleRepository.save(new Vehicle("def", "automatic", "disabled", new Speed("mph", 150.0))).block();
        track.setVehicles(Arrays.asList(vehicle1, vehicle2));
        trackRepository.save(track).block();
    }

    private void verifyLoadedTrack(Track loadedTrack) {
        assertNotNull(loadedTrack.getId());
        assertThat(loadedTrack.getName(), is("abc"));
        assertThat(loadedTrack.getDescription(), is("a long description"));
        assertThat(loadedTrack.getLength().getUnit(), is("km"));
        assertThat(loadedTrack.getLength().getValue(), is(7.4));
        assertThat(loadedTrack.getVehicles(), hasSize(2));
        verifyLoadedVehicle(loadedTrack.getVehicles().get(0));
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
    public void givenTrackIsCreated_whenRetrieve_thenFindTheTrack() {
        saveTheTrack();

        Mono<Track> trackMono = trackRepository.findById(track.getId());

        StepVerifier
                .create(trackMono)
                .assertNext(this::verifyLoadedTrack)
                .expectComplete()
                .verify();
    }

    @Test
    public void givenTrackIsCreated_whenUpdate_thenTrackIsUpdated() {
        saveTheTrack();

        track.setLength(new Length("inch", 1000.0));
        trackRepository.save(track).block();

        Mono<Track> trackMono = trackRepository.findById(track.getId());
        StepVerifier
                .create(trackMono)
                .assertNext(loadedTrack -> {
                    assertThat(loadedTrack.getLength().getUnit(), is("inch"));
                    assertThat(loadedTrack.getLength().getValue(), is(1000.0));
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void givenTrackIsCreated_whenDelete_thenTrackIsNotFoundAnymore() {
        saveTheTrack();

        trackRepository.deleteById(track.getId()).block();

        Track loadedTrack = trackRepository.findById(track.getId()).block();
        assertThat(loadedTrack, is(nullValue()));
    }

    @Test
    public void given2TracksAreCreated_whenFindAll_thenFindTheVehicles() {
        saveTwoTracks();

        Flux<Track> trackFlux = trackRepository.findAll();

        AtomicInteger count = new AtomicInteger(0);
        StepVerifier
                .create(trackFlux)
                .thenConsumeWhile(track -> {
                    count.incrementAndGet();
                    return true;
                })
                .expectComplete()
                .verify();
        assertThat(count.get(), is(2));
    }

    private void saveTwoTracks() {
        saveTheTrack();
        trackRepository.save(secondTrack).block();
    }

    @Test
    public void given2TracksAreCreated_whenFindByFirstCode_thenFirstVehicleShouldBeFound() {
        saveTwoTracks();

        Flux<Track> trackFlux = trackRepository.findAllByDynamicField("name", "abc");

        AtomicInteger count = new AtomicInteger(0);
        StepVerifier
                .create(trackFlux)
                .thenConsumeWhile(track -> {
                    verifyLoadedTrack(track);
                    count.incrementAndGet();
                    return true;
                })
                .expectComplete()
                .verify();
        assertThat(count.get(), is(1));
    }
}