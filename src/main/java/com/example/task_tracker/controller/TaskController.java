package com.example.task_tracker.controller;

import com.example.task_tracker.mapper.TaskMapper;
import com.example.task_tracker.model.TaskResponse;
import com.example.task_tracker.model.UpsertTaskRequest;
import com.example.task_tracker.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    @GetMapping
    public Flux<TaskResponse> getAllTasks() {
        return taskService.findAll().map(taskMapper::taskToResponse);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> getTaskById(@PathVariable String id) {
        return taskService.findById(id)
                .map(taskMapper::taskToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<TaskResponse>> createTask(@RequestBody @Valid UpsertTaskRequest request) {
        return taskService.create(taskMapper.responseToTask(request))
                .map(taskMapper::taskToResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> updateTask(@PathVariable String id,
                                                         @RequestBody @Valid UpsertTaskRequest request) {
        return taskService.update(id, taskMapper.responseToTask(request))
                .map(taskMapper::taskToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
        return taskService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PutMapping("/{taskId}/{userId}")
    public Mono<ResponseEntity<TaskResponse>> addObserverToTask(@PathVariable String taskId,
                                                                @PathVariable String userId) {
        return taskService.addObserver(taskId, userId)
                .map(taskMapper::taskToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}