package ru.rpuch.demo.reactivemongo.service;

import reactor.core.publisher.Mono;
import ru.rpuch.demo.reactivemongo.domain.Track;
import ru.rpuch.demo.reactivemongo.domain.Tracks;

/**
 * @author rpuch
 */
public interface TrackService {
    Mono<Track> createTrack(Track track);

    Mono<Track> retrieveTrack(String trackId);

    Mono<Track> updateTrack(String trackId, Track newTrack);

    Mono<DeletionStatus> deleteTrack(String trackId);

    Mono<Tracks> listTracks();

    Mono<Tracks> searchTracks(FieldExample example);
}
