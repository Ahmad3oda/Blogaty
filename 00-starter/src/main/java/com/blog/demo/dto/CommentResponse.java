package com.blog.demo.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentResponse {
    private int id;
    private int userId;
    private int blogId;
    private String content;
    private LocalDateTime date;
    private int votes;

    private String username;
}
