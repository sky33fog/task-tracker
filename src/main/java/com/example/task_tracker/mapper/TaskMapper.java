package com.example.task_tracker.mapper;

import com.example.task_tracker.entity.Task;
import com.example.task_tracker.model.TaskResponse;
import com.example.task_tracker.model.UpsertTaskRequest;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@DecoratedWith(TaskMapperDelegate.class)
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = UserMapper.class)
public interface TaskMapper {

    Task requestToTask(UpsertTaskRequest upsertTaskRequest);

    @Mapping(source = "taskId", target = "id")
    Task requestToTask(String taskId, UpsertTaskRequest upsertTaskRequest);

    TaskResponse taskToResponse(Task task);

    Task responseTotask(UpsertTaskRequest request);
}
