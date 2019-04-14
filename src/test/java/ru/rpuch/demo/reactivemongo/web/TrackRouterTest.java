package ru.rpuch.demo.reactivemongo.web;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.rpuch.demo.reactivemongo.domain.Vehicle;
import ru.rpuch.demo.reactivemongo.domain.Length;
import ru.rpuch.demo.reactivemongo.domain.Speed;
import ru.rpuch.demo.reactivemongo.domain.Track;
import ru.rpuch.demo.reactivemongo.domain.Tracks;
import ru.rpuch.demo.reactivemongo.repository.VehicleRepository;
import ru.rpuch.demo.reactivemongo.repository.TrackRepository;

import java.util.Arrays;
import java.util.Collections;
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
public class TrackRouterTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private TrackRepository trackRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    private Track track = new Track("abc", "a long description", new Length("km", 7.4),
            Arrays.asList(
                    new Vehicle("abc", "manual", "enabled", new Speed("kph", 200.0)),
                    new Vehicle("def", "automatic", "disabled", new Speed("mph", 150.0))
            ));
    private Track secondTrack = new Track("def", "a short description", new Length("ft", 500.0),
            Collections.emptyList());

    @After
    public void cleanup() {
        trackRepository.deleteAll().block();
        vehicleRepository.deleteAll().block();
    }

    @Test
    public void givenCollectionIsEmpty_whenATrackIsPosted_thenItShouldBeCreatedAndReturned() {
        Track returnedTrack = webTestClient
                .post().uri("/track")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(
                        TestResources.resourceAsString("track-to-create.json", getClass())
                ))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Track.class).value(this::verifyCreatedTrack)
                .returnResult()
                .getResponseBody();

        Track loadedTrack = trackRepository.findById(returnedTrack.getId()).block();
        assertThat(loadedTrack, is(notNullValue()));
        verifyCreatedTrack(loadedTrack);
    }

    private void verifyCreatedTrack(Track trackToVerify) {
        assertThat(trackToVerify.getId(), is(notNullValue()));
        assertThat(trackToVerify.getName(), is("abc"));
        assertThat(trackToVerify.getDescription(), is("a long description"));
        assertThat(trackToVerify.getLength().getUnit(), is("km"));
        assertThat(trackToVerify.getLength().getValue(), is(7.4));
        assertThat(trackToVerify.getVehicles(), hasSize(2));
        assertThatVehicleDataIsSameAs(trackToVerify.getVehicles().get(0),
                new Vehicle("abc", "manual", "enabled", new Speed("kph", 200.0)));
        assertThatVehicleDataIsSameAs(trackToVerify.getVehicles().get(1),
                new Vehicle("def", "automatic", "disabled", new Speed("mph", 150.0)));
    }

    private void assertThatVehicleDataIsSameAs(Vehicle vehicle1, Vehicle vehicle2) {
        assertThat(vehicle1.getCode(), is(vehicle2.getCode()));
        assertThat(vehicle1.getTransmission(), is(vehicle2.getTransmission()));
        assertThat(vehicle1.getAi(), is(vehicle2.getAi()));
        assertThat(vehicle1.getMaxSpeed(), is(vehicle2.getMaxSpeed()));
    }

    private void verifyTracks(List<Track> tracks, int expectedCount) {
        assertThat(tracks, hasSize(expectedCount));
    }

    @Test
    public void givenATrackExists_whenATrackIsRetrieved_thenItShouldBeReturned() {
        saveFirstTrack();

        webTestClient
                .get().uri("/track/" + track.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Track.class).value(this::verifyCreatedTrack);
    }

    private void saveFirstTrack() {
        saveATrack(track);
    }

    private void saveATrack(Track track) {
        vehicleRepository.saveAll(track.getVehicles()).collectList().block();
        trackRepository.save(track).block();
    }

    @Test
    public void givenNoTrackExists_whenATrackIsRetrieved_then404ShouldBeReturned() {
        webTestClient
                .get().uri("/track/no-such-track")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void givenATrackExists_whenATrackIsPut_thenItShouldBeUpdatedAndReturned() {
        saveFirstTrack();

        webTestClient
                .put().uri("/track/" + track.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(
                        TestResources.resourceAsString("track-to-update.json", getClass())
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Track.class).value(this::verifyUpdatedTrack);

        Track loadedTrack = trackRepository.findById(track.getId()).block();
        assertThat(loadedTrack, is(notNullValue()));
        verifyUpdatedTrack(loadedTrack);
    }

    @Test
    public void givenNoTrackExists_whenATrackIsPut_then404ShouldBeReturned() {
        webTestClient
                .put().uri("/track/no-such-track")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(
                        TestResources.resourceAsString("track-to-update.json", getClass())
                ))
                .exchange()
                .expectStatus().isNotFound();
    }

    private void verifyUpdatedTrack(Track trackToVerify) {
        assertThat(trackToVerify.getName(), is("def"));
        assertThat(trackToVerify.getDescription(), is("a short description"));
    }

    @Test
    public void givenATrackExists_whenATrackIsDeleted_thenItShouldBeDeleted() {
        saveFirstTrack();

        webTestClient
                .delete().uri("/track/" + track.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

        Track loadedTrack = trackRepository.findById(track.getId()).block();
        assertThat(loadedTrack, is(nullValue()));
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
    public void given2TracksExist_whenTracksAreListed_then2TracksAreReturned() {
        saveTwoTracks();

        webTestClient
                .get().uri("/track")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Tracks.class)
                .value(tracks -> verifyTracks(tracks.getTracks(), 2));
    }

    private void saveTwoTracks() {
        saveFirstTrack();
        saveATrack(secondTrack);
    }

    @Test
    public void given2TracksExist_whenTracksAreSearchByName_then1TrackIsReturned() {
        saveTwoTracks();

        webTestClient
                .get().uri("/track?field=name&value=abc")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Tracks.class)
                .value(tracks -> verifyTracks(tracks.getTracks(), 1));
    }
}