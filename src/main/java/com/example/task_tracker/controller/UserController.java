package com.example.task_tracker.controller;

import com.example.task_tracker.mapper.UserMapper;
import com.example.task_tracker.model.UpsertUserRequest;
import com.example.task_tracker.model.UserResponse;
import com.example.task_tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    public Flux<UserResponse> getAllUsers() {
        return userService.findAll()
                .map(userMapper::userToResponse);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getUserById(@PathVariable String id) {
        return userService.findById(id)
                .map(userMapper::userToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<UserResponse>> createUser(@RequestBody @Valid UpsertUserRequest request) {
        return userService.save(userMapper.requestToUser(request))
                .map(userMapper::userToResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable String id,
                                                         @RequestBody @Valid UpsertUserRequest request) {
        return userService.update(id, userMapper.requestToUser(request))
                .map(userMapper::userToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return userService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}