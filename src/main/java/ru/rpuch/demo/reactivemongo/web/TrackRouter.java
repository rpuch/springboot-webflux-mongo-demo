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
public class TrackRouter {
    @Bean
   	public RouterFunction<ServerResponse> trackRoutes(TrackHandler trackHandler) {
    	return route()
				.GET("/track", trackHandler::listTracks)
				.GET("/track/{id}", trackHandler::retrieveTrack)
				.POST("/track", acceptApplicationJson(), trackHandler::createTrack)
				.PUT("/track/{id}", acceptApplicationJson(), trackHandler::updateTrack)
				.DELETE("/track/{id}", trackHandler::deleteTrack)
				.build();
   	}
}
