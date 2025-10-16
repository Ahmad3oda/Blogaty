package com.blog.demo.dto;

import com.blog.demo.entity.Blog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogResponse {
    private int blogId;
    private UserResponse user;
    private String content;
    private LocalDateTime date;
    private int votes;
    private int comments;

    public BlogResponse(Blog blog) {
        this.blogId = blog.getBlogId();
        this.user = new UserResponse(blog.getUser());
        this.content = blog.getContent();
        this.date = blog.getDate();
        this.votes = blog.getVotes();
        this.comments = blog.getComments();
    }
}
