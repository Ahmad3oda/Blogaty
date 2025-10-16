package com.blog.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    private String content;
    private LocalDateTime date;
    private int votes;

    public Comment(User user, Blog blog, String content, LocalDateTime now, int i) {
        this.user = user;
        this.blog = blog;
        this.content = content;
        this.date = now;
        this.votes = i;
    }

    public Comment(int commentId) {
        this.id = commentId;
    }
}
