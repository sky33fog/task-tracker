package com.example.task_tracker.configuration;

import com.example.task_tracker.handler.TaskHandler;
import com.example.task_tracker.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class Router {

    @Bean
    public RouterFunction<ServerResponse> userRouters(UserHandler userHandler) {
        return RouterFunctions.route()
                .GET("/api/user", userHandler::findAllUsers)
                .GET("/api/user/{id}", userHandler::findById)
                .POST("/api/user", userHandler::createUser)
                .PUT("/api/user/{id}", userHandler::updateUser)
                .DELETE("/api/user/{id}", userHandler::deleteUserById)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> taskRouters(TaskHandler taskHandler) {
        return RouterFunctions.route()
                .GET("/api/task", taskHandler::findAllTasks)
                .GET("/api/task/{id}", taskHandler::findById)
                .POST("/api/task", taskHandler::createTask)
                .PUT("/api/task/{id}", taskHandler::updateTask)
                .DELETE("/api/task/{id}", taskHandler::deleteTask)
                .PUT("/api/task/{taskId}/{userId}", taskHandler::addObserver)
                .build();
    }
}
