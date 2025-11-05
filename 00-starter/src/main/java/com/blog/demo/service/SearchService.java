package com.blog.demo.service;

import com.blog.demo.dto.BlogResponse;
import com.blog.demo.dto.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SearchService {
    List<UserResponse> getSearchUserResults(String searchTerm, int page, int size);
    List<BlogResponse> getSearchBlogResults(String searchTerm, int page, int size);
}
