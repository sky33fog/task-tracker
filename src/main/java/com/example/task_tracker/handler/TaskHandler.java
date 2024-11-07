package com.example.task_tracker.handler;

import com.example.task_tracker.mapper.TaskMapper;
import com.example.task_tracker.model.TaskResponse;
import com.example.task_tracker.model.UpsertTaskRequest;
import com.example.task_tracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class TaskHandler {

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    public Mono<ServerResponse> findAllTasks(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskService.findAll()
                        .map(taskMapper::taskToResponse), TaskResponse.class);
    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskService.findById(serverRequest.pathVariable("id"))
                        .map(taskMapper::taskToResponse), TaskResponse.class);
    }

    public Mono<ServerResponse> updateTask(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpsertTaskRequest.class)
                .flatMap(upsertTaskRequest ->
                    taskService.update(serverRequest.pathVariable("id"),
                            taskMapper.responseToTask(upsertTaskRequest)))
                .flatMap(task -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(taskMapper.taskToResponse(task), TaskResponse.class));
    }

    public Mono<ServerResponse> createTask(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpsertTaskRequest.class)
                .flatMap(upsertTask ->
                        taskService.create(taskMapper.responseToTask(upsertTask)))
                .flatMap(task -> ServerResponse.created(URI.create("/api/task"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(taskMapper.taskToResponse(task)), TaskResponse.class)
                );
    }

    public Mono<ServerResponse> deleteTask(ServerRequest serverRequest) {
        return taskService.deleteById(serverRequest.pathVariable("id"))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> addObserver(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskService.addObserver(
                        serverRequest.pathVariable("taskId"),
                        serverRequest.pathVariable("userId"))
                        .map(taskMapper::taskToResponse), TaskResponse.class);
    }
}