package com.example.task_tracker.model;

import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
public class UserListResponse {

    List<UserResponse> userListResponse = new ArrayList<>();
}
