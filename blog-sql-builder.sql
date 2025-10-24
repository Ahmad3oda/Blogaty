CREATE DATABASE IF NOT EXISTS `blog_directory`;
USE `blog_directory`;

DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `followers`;
DROP TABLE IF EXISTS `bookmarks`;
DROP TABLE IF EXISTS `comment_votes`;
DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `blog_votes`;
DROP TABLE IF EXISTS `blogs`;
DROP TABLE IF EXISTS `users`;

-- ========================
-- USERS
-- ========================
CREATE TABLE `users` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(20) UNIQUE NOT NULL,
    `password` VARCHAR(100) NOT NULL,
    `role` ENUM('USER', 'ADMIN') NOT NULL
);

INSERT INTO `users` (`username`, `password`, `role`) VALUES
('ouda', '1234', 'USER'),
('jimy', '1234', 'USER'),
('gom3a', '1234', 'USER'),
('admin', 'admin', 'ADMIN');

-- ========================
-- BLOGS
-- ========================
CREATE TABLE `blogs` (
    `blog_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `content` VARCHAR(200),
    `date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `votes` INT DEFAULT 0,
    `comments` INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO `blogs` (`user_id`, `content`, `date`, `votes`, `comments`) VALUES
(1, 'hello, world', NOW(), 2, 1),
(2, 'hello guys, first post here', NOW(), 1, 0),
(3, 'my first blog on this platform!', NOW(), 0, 0);

-- ========================
-- BLOG VOTES
-- ========================
CREATE TABLE `blog_votes` (
    `blog_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `type` ENUM('up', 'down') NOT NULL,
    PRIMARY KEY (blog_id, user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (blog_id) REFERENCES blogs(blog_id) ON DELETE CASCADE
);

INSERT INTO `blog_votes` VALUES
(1, 2, 'up'),
(1, 3, 'up'),
(2, 3, 'up'),
(3, 1, 'down');

-- ========================
-- COMMENTS
-- ========================
CREATE TABLE `comments` (
    `comment_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `blog_id` BIGINT NOT NULL, 
    `user_id` BIGINT NOT NULL,
    `content` VARCHAR(50),
    `date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `votes` INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (blog_id) REFERENCES blogs(blog_id) ON DELETE CASCADE
);

INSERT INTO `comments` (`blog_id`, `user_id`, `content`, `date`, `votes`) VALUES
(1, 2, 'nice post!', NOW(), 1),
(1, 3, 'good start!', NOW(), 0),
(2, 1, 'thanks!', NOW(), 0);

-- ========================
-- COMMENT VOTES
-- ========================
CREATE TABLE `comment_votes` (
    `comment_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `type` ENUM('up', 'down') NOT NULL,
    PRIMARY KEY (comment_id, user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE
);

INSERT INTO `comment_votes` VALUES
(1, 1, 'up'),
(2, 1, 'up'),
(3, 3, 'down');

-- ========================
-- BOOKMARKS
-- ========================
CREATE TABLE `bookmarks` (
    `user_id` BIGINT NOT NULL,
    `blog_id` BIGINT NOT NULL, 
    PRIMARY KEY (blog_id, user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (blog_id) REFERENCES blogs(blog_id) ON DELETE CASCADE
);

INSERT INTO `bookmarks` VALUES
(2, 1),
(3, 1),
(1, 2);

-- ========================
-- FOLLOWERS
-- ========================
CREATE TABLE `followers` (
    `user_id` BIGINT NOT NULL,
    `follower_id` BIGINT NOT NULL, 
    PRIMARY KEY (user_id, follower_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO `followers` VALUES
(1, 2),
(2, 3),
(3, 1);

-- ========================
-- NOTIFICATIONS
-- ========================
CREATE TABLE `notifications` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `receiver_id` BIGINT NOT NULL,
    `actor_id` BIGINT NOT NULL,
    `type` ENUM('BLOG_VOTED', 'COMMENTED', 'COMMENT_VOTED', 'FOLLOWED') NOT NULL,
    `target_id` BIGINT,
    `target_type` ENUM('BLOG', 'COMMENT', 'USER'),
    `message` VARCHAR(255),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `is_read` BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO `notifications` 
(`receiver_id`, `actor_id`, `type`, `target_id`, `target_type`, `message`) VALUES
(1, 2, 'COMMENTED', 1, 'BLOG', 'jimy commented on your blog.'),
(1, 3, 'BLOG_VOTED', 1, 'BLOG', 'gom3a upvoted your blog.'),
(2, 1, 'COMMENT_VOTED', 1, 'COMMENT', 'ouda upvoted your comment.'),
(3, 2, 'FOLLOWED', 2, 'USER', 'jimy started following you.');
