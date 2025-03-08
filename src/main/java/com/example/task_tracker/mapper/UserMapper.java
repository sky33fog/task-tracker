package com.example.task_tracker.mapper;

import com.example.task_tracker.entity.User;
import com.example.task_tracker.model.UpsertUserRequest;
import com.example.task_tracker.model.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User requestToUser(UpsertUserRequest request);

    @Mapping(source = "userId", target = "id")
    User requestToUser(String userId, UpsertUserRequest request);

    UserResponse userToResponse(User user);

}