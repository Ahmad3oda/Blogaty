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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 200)
    private String content;

    private LocalDateTime date;
    private int votes;
    private int comments;

    public Blog(User user, String content, LocalDateTime now, int i, int i1) {
        this.user = user;
        this.content = content;
        this.date = now;
        this.votes = i;
        this.comments = i1;
    }

    public Blog(int blogId){
        this.blogId = blogId;
    }
}
