package com.example.task_tracker.handler;


import com.example.task_tracker.mapper.UserMapper;
import com.example.task_tracker.model.UpsertUserRequest;
import com.example.task_tracker.model.UserResponse;
import com.example.task_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;


@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserService userService;

    private final UserMapper userMapper;

    public Mono<ServerResponse> findAllUsers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findAll()
                        .map(userMapper::userToResponse), UserResponse.class);
    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findById(serverRequest.pathVariable("id"))
                        .map(userMapper::userToResponse), UserResponse.class);
    }

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpsertUserRequest.class)
                .flatMap(upsertUser ->
                        userService.save(userMapper.requestToUser(upsertUser)))
                .flatMap(user -> ServerResponse.created(URI.create("/api/user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(userMapper.userToResponse(user)), UserResponse.class));
    }

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpsertUserRequest.class)
                .flatMap(updatedUser ->
                        userService.update(serverRequest.pathVariable("id"), userMapper.requestToUser(updatedUser)))
                .flatMap(user -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(userMapper.userToResponse(user)), UserResponse.class));
    }

    //todo добавить в конспект что код метода хандлера должен реализовывать потоковую обработку. иначе
    // почему-то неработает
    public Mono<ServerResponse> deleteUserById(ServerRequest serverRequest) {
        return userService.deleteById(serverRequest.pathVariable("id"))
                .then(ServerResponse.noContent().build());
    }
}