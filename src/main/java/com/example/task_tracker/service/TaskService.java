package com.example.task_tracker.service;

import com.example.task_tracker.entity.Task;
import com.example.task_tracker.entity.User;
import com.example.task_tracker.repository.TaskRepository;
import com.example.task_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public Flux<Task> findAll() {
        return taskRepository.findAll();
    }

    public Mono<Task> findById(String id) {
        Mono<Task> monoTaskFromDb = taskRepository.findById(id);

        Mono<Task> monoTaskWithAuthorAndAssignee = monoTaskFromDb
                .zipWhen(task ->
                        userRepository.findById(task.getAuthorId()), Tuples::of)
                .zipWhen(tuple -> userRepository.findById(tuple.getT1().getAssigneeId()), (tuple, assignee) -> {
                            tuple.getT1().setAuthor(tuple.getT2());
                            tuple.getT1().setAssignee(assignee);
                            return tuple.getT1();
                        });

        Mono<Task> monoTaskWithObserverSet = monoTaskWithAuthorAndAssignee.map(t -> {
                    Set<User> obsSet = new HashSet<>();
                    t.getObserverIds().forEach(obsId -> userRepository.findById(obsId).subscribe(obsSet::add));
                    t.setObservers(obsSet);
                    return t;
                });

        return monoTaskWithObserverSet
//                .delayElement(Duration.ofMillis(20))
                ;
    }

    public Mono<Task> create(Task task) {
        task.setId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
        return Mono.zip(
                userRepository.findById(task.getAuthorId()),
                userRepository.findById(task.getAssigneeId()),
                (o1, o2) -> {
                    task.setAuthor(o1);
                    task.setAssignee(o2);
                    return task;
                }
        ).flatMap(t -> {
            Set<User> obsSet = new HashSet<>();
            t.getObserverIds().forEach(obsId -> userRepository.findById(obsId).subscribe(obsSet::add));
            t.setObservers(obsSet);
            return taskRepository.save(t);
        });
    }

    public Mono<Task> update(String id, Task task) {
        return taskRepository.findById(id).flatMap(updatedTask -> {
            if (StringUtils.hasText(task.getName())) {
                updatedTask.setName(task.getName());
            }
            if (StringUtils.hasText(task.getDescription())) {
                updatedTask.setDescription(task.getDescription());
            }
            if (task.getStatus() != null) {
                updatedTask.setStatus(task.getStatus());
            }
            if (task.getAuthor() != null) {
                updatedTask.setAuthor(task.getAuthor());
                updatedTask.setAuthorId(task.getAuthor().getId());
            }
            if (task.getAssignee() != null) {
                updatedTask.setAssignee(task.getAssignee());
                updatedTask.setAssigneeId(task.getAssignee().getId());
            }
            if (task.getObservers() != null) {
                updatedTask.setObservers(task.getObservers());
                updatedTask.setObserverIds(task.getObservers().stream().map(User::getId).collect(Collectors.toSet()));
            }
            updatedTask.setUpdatedAt(Instant.now());
            return taskRepository.save(updatedTask);
        });
    }

    public Mono<Task> addObserver(String taskId, String userId) {
        return taskRepository.findById(taskId).flatMap(updatedTask -> {
            userRepository.findById(userId).subscribe(user -> updatedTask.getObservers().add(user));
            return taskRepository.save(updatedTask);
        });
    }

    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }
}
