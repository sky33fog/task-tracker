package com.example.task_tracker.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String username;

    private String password;

    private String email;

    @Field("roles")
    private Set<RoleType> roles = new HashSet<>();

    private List<String> commentList = new ArrayList<>();

    public List<SimpleGrantedAuthority> getAuthorities() {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.name())).toList();
    }
}