package com.blog.demo.dto;

import com.blog.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {

    private Long id;
    private String username;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }
}
