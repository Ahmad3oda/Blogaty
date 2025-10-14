package com.blog.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FollowResponse {
    public int followersCount;
    public List<UserResponse> followers;
}
