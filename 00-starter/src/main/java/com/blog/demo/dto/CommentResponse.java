package com.blog.demo.dto;

import com.blog.demo.entity.Comment;
import com.blog.demo.entity.User;
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
    private UserResponse author;
    private int blogId;
    private String content;
    private LocalDateTime date;
    private int votes;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.author = new UserResponse(comment.getUser());
        this.blogId = comment.getBlog().getBlogId();
        this.content = comment.getContent();
        this.date = comment.getDate();
        this.votes = comment.getVotes();
    }
}
