package com.example.task_tracker.service;

import com.example.task_tracker.entity.Task;
import com.example.task_tracker.entity.User;
import com.example.task_tracker.exception.EntityNotFoundException;
import com.example.task_tracker.repository.TaskRepository;
import com.example.task_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public Flux<Task> findAll() {
        return taskRepository.findAll().flatMap(this::getFilledMonoTask);
    }

    public Mono<Task> findById(String id) {
        return taskRepository.findById(id).flatMap(this::getFilledMonoTask);
    }

    public Mono<Task> create(Task task) {
        task.setId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());

        return getFilledMonoTask(task).flatMap(taskRepository::save);
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
            if (!task.getObserverIds().isEmpty()) {
                updatedTask.setObserverIds(task.getObserverIds());
            }
            updatedTask.setUpdatedAt(Instant.now());

            return getFilledMonoTask(updatedTask).flatMap(taskRepository::save);
        });
    }

    public Mono<Task> addObserver(String taskId, String userId) {
        Mono<Task> monoTask = findById(taskId);
        Mono<User> monoUser = userRepository.findById(userId);

       return Mono.zip(monoTask, monoUser)
                .flatMap(tuple -> {
                    tuple.getT1().getObservers().add(tuple.getT2());
                    tuple.getT1().getObserverIds().add(tuple.getT2().getId());
                    return taskRepository.save(tuple.getT1());
                });
    }

    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }

    private Mono<Task> getFilledMonoTask(Task task) {
        return Mono.zip(
                userRepository.findById(task.getAuthorId())
                        .defaultIfEmpty(new User(null, null, null)),
                userRepository.findById(task.getAssigneeId())
                        .defaultIfEmpty(new User(null, null, null)),
                getMonoUserList(task))
                .map(tuple -> {
                    if (tuple.getT1().getId() == null) {
                        throw  new EntityNotFoundException(MessageFormat
                                .format("Автор c ID {0} не найден!", task.getAuthorId()));
                    }
                    task.setAuthor(tuple.getT1());

                    if(tuple.getT2().getId() != null) {
                        task.setAssignee(tuple.getT2());
                    }

                    task.getObservers().addAll(tuple.getT3());
                    return task;
                });
    }

    private Mono<List<User>> getMonoUserList(Task task) {
        return userRepository.findAllById(task.getObserverIds()).collectList();
    }
}