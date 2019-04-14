package ru.rpuch.demo.reactivemongo.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static ru.rpuch.demo.reactivemongo.web.Routes.acceptApplicationJson;

/**
 * @author rpuch
 */
@Configuration
public class VehicleRouter {
    @Bean
   	public RouterFunction<ServerResponse> vehicleRoutes(VehicleHandler vehicleHandler) {
    	return route()
				.GET("/vehicle", vehicleHandler::listVehicles)
				.GET("/vehicle/{id}", vehicleHandler::retrieveVehicle)
				.POST("/vehicle", acceptApplicationJson(), vehicleHandler::createVehicle)
				.PUT("/vehicle/{id}", acceptApplicationJson(), vehicleHandler::updateVehicle)
				.DELETE("/vehicle/{id}", vehicleHandler::deleteVehicle)
				.build();
   	}
}
