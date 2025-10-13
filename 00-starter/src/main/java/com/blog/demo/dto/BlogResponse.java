package com.blog.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogResponse {
    private int blogId;
    private int userId;
    private String content;
    private LocalDateTime date;
    private int votes;
    private int comments;

    private String username;
}
