package com.blog.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment_votes")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentVote {
    @EmbeddedId
    private CommentVoteID id;

    @Enumerated(EnumType.STRING)
    private Vote type;
}
