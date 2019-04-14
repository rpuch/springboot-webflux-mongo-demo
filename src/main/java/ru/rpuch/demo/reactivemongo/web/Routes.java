package ru.rpuch.demo.reactivemongo.web;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * @author rpuch
 */
class Routes {
    static RequestPredicate acceptApplicationJson() {
        return accept(MediaType.APPLICATION_JSON);
    }
}
