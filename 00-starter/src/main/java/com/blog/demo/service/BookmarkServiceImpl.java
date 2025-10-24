package com.blog.demo.service;

import com.blog.demo.RedisConfig;
import com.blog.demo.dto.BlogResponse;
import com.blog.demo.dto.BookmarkResponse;
import com.blog.demo.entity.Bookmark;
import com.blog.demo.entity.BookmarkID;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.BlogRepository;
import com.blog.demo.repository.BookmarkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookmarkServiceImpl implements BookmarkService {

    RedisConfig cache;
    BookmarkRepository bookmarkRepository;
    BlogRepository blogRepository;
    BlogService blogService;
    public BookmarkServiceImpl(RedisConfig cache,
                               BookmarkRepository bookmarkRepository,
                               BlogRepository blogRepository,
                               BlogService blogService) {
        this.cache = cache;
        this.bookmarkRepository = bookmarkRepository;
        this.blogRepository = blogRepository;
        this.blogService = blogService;
    }

    private BookmarkResponse getBookmarks(List<Long> bookmarksIds) {
        List<BlogResponse> bookmarks = new ArrayList<>();
        bookmarksIds.forEach(bookmarkId -> bookmarks.add(blogService.findByBlogId(Math.toIntExact(bookmarkId))));
        return new BookmarkResponse(bookmarks);
    }

    @Override
    public BookmarkResponse getBookmarksByUserId(int userId) {
        List <Long> bookmarksIds = bookmarkRepository.findBlogIdByUserId(userId);
        return getBookmarks(bookmarksIds);
    }

    @Override
    public void addBookmark(int userId, int blogId) {
        Bookmark bookmark = bookmarkRepository.findById_UserIdAndId_BlogId(userId, blogId);
        if(bookmark != null) {
            throw new GlobalException("Bookmark already exists - user id: " + userId+ ", blog id: " + blogId);
        }
        bookmarkRepository.save(new Bookmark(new BookmarkID(userId, blogId)));
    }

    @Override
    public void deleteBookmark(int userId, int blogId) {
        bookmarkRepository.delete(new Bookmark(new BookmarkID(userId, blogId)));
    }
}
