package ru.rpuch.demo.reactivemongo.web;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.rpuch.demo.reactivemongo.domain.Track;
import ru.rpuch.demo.reactivemongo.service.TrackService;

/**
 * @author rpuch
 */
@Component
public class TrackHandler {
	private final TrackService trackService;
	private final SearchHandlers searchHandlers = new SearchHandlers();

	public TrackHandler(TrackService trackService) {
		this.trackService = trackService;
	}

    Mono<ServerResponse> createTrack(ServerRequest request) {
		return request.bodyToMono(Track.class)
				.flatMap(trackService::createTrack)
				.flatMap(ServerResponses::jsonCreatedWithBody);
   	}

	Mono<ServerResponse> retrieveTrack(ServerRequest request) {
		String id = request.pathVariable("id");
		return trackService.retrieveTrack(id)
				.flatMap(ServerResponses::jsonOkWithBody)
				.switchIfEmpty(ServerResponse.notFound().build());
	}

	Mono<ServerResponse> updateTrack(ServerRequest request) {
		String id = request.pathVariable("id");
		return request.bodyToMono(Track.class)
				.flatMap(track -> trackService.updateTrack(id, track))
				.flatMap(ServerResponses::jsonOkWithBody)
				.switchIfEmpty(ServerResponse.notFound().build());
	}

	Mono<ServerResponse> deleteTrack(ServerRequest request) {
		return trackService.deleteTrack(request.pathVariable("id"))
				.flatMap(deletionStatus -> deletionStatus.deleted()
						? ServerResponse.noContent().build()
						: ServerResponse.notFound().build());
	}

	Mono<ServerResponse> listTracks(ServerRequest request) {
		return searchHandlers.extractFieldExample(request)
				.map(trackService::searchTracks)
				.orElse(trackService.listTracks())
				.flatMap(ServerResponses::jsonOkWithBody);
	}
}
