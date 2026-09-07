package com.blog.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "blog_votes")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogVote {
    @EmbeddedId
    private BlogVoteID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("blogId")
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Vote type;
}
