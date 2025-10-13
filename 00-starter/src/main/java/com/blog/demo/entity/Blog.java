package com.blog.demo.entity;

import com.blog.demo.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private int blogId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(length = 200)
    private String content;

    private LocalDateTime date;
    private int votes;
    private int comments;

}
