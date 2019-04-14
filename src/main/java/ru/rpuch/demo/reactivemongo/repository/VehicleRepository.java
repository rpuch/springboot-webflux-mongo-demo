package ru.rpuch.demo.reactivemongo.repository;

import org.springframework.stereotype.Repository;
import ru.rpuch.demo.reactivemongo.domain.Vehicle;

/**
 * @author rpuch
 */
@Repository
public interface VehicleRepository extends SearchableRepository<Vehicle, String> {
}
