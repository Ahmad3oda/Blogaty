package com.blog.demo.controller;

import com.blog.demo.dto.BlogResponse;
import com.blog.demo.dto.UserResponse;
import com.blog.demo.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {
    private SearchService searchService;

    @Autowired
    public void setUserService(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers(@RequestBody Map<String, String> searchParam,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return searchService.getSearchUserResults(searchParam.get("search"), page, size);
    }

    @GetMapping("/blogs")
    public List<BlogResponse> getBlogs(@RequestBody Map<String, String> searchParam,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return searchService.getSearchBlogResults(searchParam.get("search"), page, size);
    }

}
