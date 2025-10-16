package com.blog.demo.dto;

import com.blog.demo.entity.BlogVote;
import com.blog.demo.entity.User;
import com.blog.demo.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogVoteResponse {
    public UserResponse user;
    public Vote vote;

    public BlogVoteResponse (BlogVote blogVote){
        this.user = new UserResponse(blogVote.getId().getUser());
        this.vote = blogVote.getType();
    }
}
