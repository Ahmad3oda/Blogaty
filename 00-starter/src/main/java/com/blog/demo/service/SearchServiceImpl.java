package com.blog.demo.service;

import com.blog.demo.dto.BlogResponse;
import com.blog.demo.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final UserService userService;
    private final BlogService blogService;

    @Override
    public List<UserResponse> getSearchUserResults(String searchTerm, int page, int size) {
        return userService.findByUsernamePart(searchTerm, page, size);
    }

    @Override
    public List<BlogResponse> getSearchBlogResults(String searchTerm, int page, int size) {
        return blogService.findByContent(searchTerm, page, size);
    }
}
