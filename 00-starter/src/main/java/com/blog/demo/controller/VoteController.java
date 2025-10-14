package com.blog.demo.controller;

import com.blog.demo.dto.BlogVoteRequest;
import com.blog.demo.dto.BlogVoteResponse;
import com.blog.demo.dto.CommentVoteRequest;
import com.blog.demo.dto.CommentVoteResponse;
import com.blog.demo.entity.BlogVoteID;
import com.blog.demo.entity.Vote;
import com.blog.demo.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/votes")
public class VoteController {

    VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping("/blog/{blogId}")
    public List<BlogVoteResponse> getBlogVotes(@PathVariable Long blogId) {
        return voteService.findAllBlogVotesByBlogId(blogId);
    }

    @PostMapping("/blog/{userId}/{blogId}")
    public BlogVoteResponse addBlogVote(@PathVariable int userId,
                                        @PathVariable int blogId,
                                        @RequestBody BlogVoteRequest blogVoteRequest) {
        return voteService.addBlogVote(userId, blogId, blogVoteRequest);
    }

    @PatchMapping("/blog/{userId}/{blogId}")
    public BlogVoteResponse updateBlogVote(@PathVariable int userId,
                                           @PathVariable int blogId,
                                           @RequestBody BlogVoteRequest blogVoteRequest) {
        return voteService.updateBlogVote(userId, blogId, blogVoteRequest);
    }

//    @DeleteMapping("/blog/{userId}/{blogId}")
//    public void deleteBlogVote(@PathVariable int userId,
//                               @PathVariable int blogId){
//        BlogVoteID vote = new BlogVoteID(); vote.setUserId(userId); vote.setBlogId(blogId);
//        voteService.deleteBlogVote(vote);
//    }

    @GetMapping("/comment/{commentId}")
    public List<CommentVoteResponse> getCommentVotes(@PathVariable Long commentId) {
        return voteService.findAllCommentVotesByCommentId(commentId);
    }

    @PostMapping("/comment/{userId}/{commentId}")
    public CommentVoteResponse addCommentVote(@PathVariable int userId,
                                        @PathVariable int commentId,
                                        @RequestBody CommentVoteRequest commentVoteRequest) {
        return voteService.addCommentVote(userId, commentId, commentVoteRequest);
    }

    @PatchMapping("/comment/{userId}/{commentId}")
    public CommentVoteResponse updateCommentVote(@PathVariable int userId,
                                              @PathVariable int commentId,
                                              @RequestBody CommentVoteRequest commentVoteRequest) {
        return voteService.updateCommentVote(userId, commentId, commentVoteRequest);
    }
}
