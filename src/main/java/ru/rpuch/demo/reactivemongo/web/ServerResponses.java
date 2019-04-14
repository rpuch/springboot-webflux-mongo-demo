package ru.rpuch.demo.reactivemongo.web;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author rpuch
 */
class ServerResponses {
    static Mono<ServerResponse> jsonOkWithBody(Object object) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(object));
    }

    static Mono<ServerResponse> jsonCreatedWithBody(Object object) {
        return ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(object));
    }
}
