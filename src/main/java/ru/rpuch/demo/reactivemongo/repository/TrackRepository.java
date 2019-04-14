package ru.rpuch.demo.reactivemongo.repository;

import org.springframework.stereotype.Repository;
import ru.rpuch.demo.reactivemongo.domain.Track;

/**
 * @author rpuch
 */
@Repository
public interface TrackRepository extends SearchableRepository<Track, String> {
}
