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

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Vote type;
}
