package com.blog.demo.dto;

import com.blog.demo.entity.CommentVote;
import com.blog.demo.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentVoteResponse {
    private UserResponse user;
    public Vote vote;

    public CommentVoteResponse(CommentVote commentVote){
        this.user = new UserResponse(commentVote.getId().getUser());
        this.vote = commentVote.getType();
    }
}
