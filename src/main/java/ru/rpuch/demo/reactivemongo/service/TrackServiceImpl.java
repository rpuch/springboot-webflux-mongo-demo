package ru.rpuch.demo.reactivemongo.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpuch.demo.reactivemongo.domain.Track;
import ru.rpuch.demo.reactivemongo.domain.Tracks;
import ru.rpuch.demo.reactivemongo.repository.VehicleRepository;
import ru.rpuch.demo.reactivemongo.repository.TrackRepository;

/**
 * @author rpuch
 */
@Service
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final VehicleRepository vehicleRepository;
    private final DeleteOperation<Track, String> deleteOperation;

    public TrackServiceImpl(TrackRepository trackRepository,
            VehicleRepository vehicleRepository) {
        this.trackRepository = trackRepository;
        this.vehicleRepository = vehicleRepository;
        deleteOperation = new DeleteOperation<>(trackRepository);
    }

    @Override
    public Mono<Track> createTrack(Track track) {
        return vehicleRepository.saveAll(track.getVehicles()).collectList()
                .flatMap(savedVehicles -> {
                    track.setVehicles(savedVehicles);
                    return trackRepository.save(track);
                });
    }

    @Override
    public Mono<Track> retrieveTrack(String trackId) {
        return trackRepository.findById(trackId);
    }

    @Override
    public Mono<Track> updateTrack(String trackId, Track newTrack) {
        return Mono.just(newTrack)
                .filterWhen(track -> trackRepository.existsById(trackId))
                .flatMap(track -> updateExistingTrack(trackId, track));
    }

    private Mono<Track> updateExistingTrack(String trackId, Track track) {
        return vehicleRepository.saveAll(track.getVehicles()).collectList()
                .flatMap(savedVehicles -> {
                    track.setId(trackId);
                    track.setVehicles(savedVehicles);
                    return trackRepository.save(track);
                });
    }

    @Override
    public Mono<DeletionStatus> deleteTrack(String trackId) {
        return deleteOperation.delete(trackId);
    }

    @Override
    public Mono<Tracks> listTracks() {
        return collectTracks(trackRepository.findAll());
    }

    @Override
    public Mono<Tracks> searchTracks(FieldExample example) {
        Flux<Track> trackFlux = trackRepository.findAllByDynamicField(example.getField(), example.getValue());
        return collectTracks(trackFlux);
    }

    private Mono<Tracks> collectTracks(Flux<Track> trackFlux) {
        return trackFlux.collectList().map(Tracks::new);
    }
}
