package ru.rpuch.demo.reactivemongo.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Flux;

/**
 * @author rpuch
 */
@NoRepositoryBean
public interface SearchableRepository<T, ID> extends ReactiveMongoRepository<T, ID> {
    @Query("{ ?0 : ?1 }")
    Flux<T> findAllByDynamicField(String field, String value);
}
