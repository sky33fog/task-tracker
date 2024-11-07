package com.example.task_tracker.mapper;

import com.example.task_tracker.entity.Task;
import com.example.task_tracker.entity.TaskStatus;
import com.example.task_tracker.entity.User;
import com.example.task_tracker.model.UpsertTaskRequest;
import com.example.task_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public abstract class TaskMapperDelegate implements TaskMapper{

    @Autowired
    private UserRepository userRepository;

    @Override
    public Task responseTotask(UpsertTaskRequest request) {
        Task task = new Task();

        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.valueOf(request.getStatus()));
        task.setAuthorId(request.getAuthorId());
        task.setAssigneeId(request.getAssigneeId());
        task.setObserverIds(request.getObserverIds());
        task.setAuthor(userRepository.findById(request.getAuthorId()).block());
        task.setAssignee(userRepository.findById(request.getAssigneeId()).block());

        Set<User> observersSet = new HashSet<>();
        request.getObserverIds().forEach(userId -> {
            observersSet.add(userRepository.findById(userId).block());
        });
        task.setObservers(observersSet);
        return task;
    }

    @Override
    public Task responseToTask(String id, UpsertTaskRequest request) {
        Task task = responseToTask(request);

        task.setId(id);
        return task;
    }
}
