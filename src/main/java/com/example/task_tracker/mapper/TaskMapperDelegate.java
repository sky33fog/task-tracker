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
    public Task requestToTask(UpsertTaskRequest request) {
        Task task = new Task();

        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.valueOf(request.getStatus()));
        task.setAssigneeId(request.getAssigneeId());
        task.setObserverIds(request.getObserverIds());
        userRepository.findById(request.getAssigneeId()).map(u -> {
            task.setAssignee(u);
            return task;
        });

        Set<User> observersSet = new HashSet<>();

        if(request.getObserverIds() != null) {
            request.getObserverIds().forEach(userId -> {
                userRepository.findById(userId).map(o -> {
                    observersSet.add(o);
                    return observersSet;
                });
            });
        }
        task.setObservers(observersSet);
        return task;
    }

    @Override
    public Task requestToTask(String id, UpsertTaskRequest request) {
        Task task = this.requestToTask(request);

        task.setId(id);
        return task;
    }
}
