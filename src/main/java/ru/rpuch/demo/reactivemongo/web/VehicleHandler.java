package ru.rpuch.demo.reactivemongo.web;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.rpuch.demo.reactivemongo.domain.Vehicle;
import ru.rpuch.demo.reactivemongo.service.VehicleService;

/**
 * @author rpuch
 */
@Component
public class VehicleHandler {
	private final VehicleService vehicleService;
	private final SearchHandlers searchHandlers = new SearchHandlers();

	public VehicleHandler(VehicleService vehicleService) {
		this.vehicleService = vehicleService;
	}

    Mono<ServerResponse> createVehicle(ServerRequest request) {
		return request.bodyToMono(Vehicle.class)
				.flatMap(vehicleService::createVehicle)
				.flatMap(ServerResponses::jsonCreatedWithBody);
   	}

	Mono<ServerResponse> retrieveVehicle(ServerRequest request) {
		String id = request.pathVariable("id");
		return vehicleService.retrieveVehicle(id)
				.flatMap(ServerResponses::jsonOkWithBody)
				.switchIfEmpty(ServerResponse.notFound().build());
	}

	Mono<ServerResponse> updateVehicle(ServerRequest request) {
		String id = request.pathVariable("id");
		return request.bodyToMono(Vehicle.class)
				.flatMap(vehicle -> vehicleService.updateVehicle(id, vehicle))
				.flatMap(ServerResponses::jsonOkWithBody)
				.switchIfEmpty(ServerResponse.notFound().build());
	}

	Mono<ServerResponse> deleteVehicle(ServerRequest request) {
		return vehicleService.deleteVehicle(request.pathVariable("id"))
				.flatMap(deletionStatus -> deletionStatus.deleted()
						? ServerResponse.noContent().build()
						: ServerResponse.notFound().build());
	}

	Mono<ServerResponse> listVehicles(ServerRequest request) {
		return searchHandlers.extractFieldExample(request)
				.map(vehicleService::searchVehicles)
				.orElse(vehicleService.listVehicles())
				.collectList()
				.flatMap(ServerResponses::jsonOkWithBody);
	}
}
