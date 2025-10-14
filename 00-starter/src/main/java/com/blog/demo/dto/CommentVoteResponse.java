package com.blog.demo.dto;

import com.blog.demo.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentVoteResponse {
    private String username;
    public Vote vote;
}
