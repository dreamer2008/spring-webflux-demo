package com.example.demo.handler;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

public class UserHandler {

    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        Mono<User> userMono = userService.getById(Integer.parseInt(id));
        return userMono.flatMap(user ->
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(userMono)).switchIfEmpty(notFound)
        );

    }


    public Mono<ServerResponse> getAll(ServerRequest request) {
        Flux<User> users = userService.getAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(users, User.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class);
        return ServerResponse.ok().build(userService.save(userMono));
    }

    public static void main(String[] args) {
        UserService userService = new UserService();
        UserHandler userHandler = new UserHandler(userService);

        RouterFunction<ServerResponse> route = RouterFunctions
                .route(RequestPredicates.GET("/flux/user/{id}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), userHandler::getById)
                .andRoute(RequestPredicates.GET("/flux/users").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), userHandler::getAll);
        HttpHandler httpHandler = RouterFunctions.toHttpHandler(route);
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        HttpServer httpServer = HttpServer.create();
        httpServer.handle(adapter).bindNow();
    }

}
