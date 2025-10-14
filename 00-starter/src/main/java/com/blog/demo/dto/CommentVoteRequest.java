package com.blog.demo.dto;

import com.blog.demo.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentVoteRequest {
    public Vote vote;

}
