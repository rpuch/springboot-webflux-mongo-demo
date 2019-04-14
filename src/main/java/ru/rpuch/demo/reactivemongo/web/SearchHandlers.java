package ru.rpuch.demo.reactivemongo.web;

import org.springframework.web.reactive.function.server.ServerRequest;
import ru.rpuch.demo.reactivemongo.service.FieldExample;
import ru.rpuch.demo.reactivemongo.utils.Optionals;

import java.util.Optional;

/**
 * @author rpuch
 */
class SearchHandlers {
    Optional<FieldExample> extractFieldExample(ServerRequest request) {
        Optional<String> fieldOpt = request.queryParam("field");
        Optional<String> valueOpt = request.queryParam("value");
        return Optionals.merge(fieldOpt, valueOpt, FieldExample::new);
    }
}
