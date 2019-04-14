package ru.rpuch.demo.reactivemongo.service;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import static ru.rpuch.demo.reactivemongo.service.DeletionStatus.DELETED;
import static ru.rpuch.demo.reactivemongo.service.DeletionStatus.DID_NOT_EXIST;

/**
 * @author rpuch
 */
class DeleteOperation<T, ID> {
    private final ReactiveMongoRepository<T, ID> repository;

    DeleteOperation(ReactiveMongoRepository<T, ID> repository) {
        this.repository = repository;
    }

    Mono<DeletionStatus> delete(ID enittyId) {
        return Mono.just(enittyId)
                .filterWhen(repository::existsById)
                .flatMap(id -> repository.deleteById(id).then(Mono.just(DELETED)))
                .defaultIfEmpty(DID_NOT_EXIST)
                ;
    }
}
