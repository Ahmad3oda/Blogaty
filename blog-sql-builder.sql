CREATE DATABASE IF NOT EXISTS `blog_directory`;

USE `blog_directory`;

DROP TABLE IF EXISTS `followers`;
DROP TABLE IF EXISTS `bookmarks`;
DROP TABLE IF EXISTS `comment_votes`;
DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `blog_votes`;
DROP TABLE IF EXISTS `blogs`;
DROP TABLE IF EXISTS `users`;

create table `users` (
	`id` bigint primary key not null,
    `username` varchar(20) unique not null,
    `password` varchar(20)  not null
);
insert into `users` values
(1, 'ouda', '1234'),
(2, 'jimy', '1234'),
(3, 'gom3a', '1234');

create table `blogs` (
	`blog_id` bigint primary key not null,
    `user_id` bigint not null,
    `content` varchar(200),
    `date` timestamp,
    `votes` int,
    `comments` int,
    foreign key (user_id) references users(id) ON DELETE CASCADE
);
insert into `blogs` values
(1, 1, 'hello, world', null, 2, 1),
(2, 2, 'hello guys, first post here', null, 1, 0);

create table `blog_votes` (
	`blog_id` bigint not null,
    `user_id` bigint not null,
    `type` enum("up", "down") not null,
    foreign key (user_id) references users(id) ON DELETE CASCADE,
    foreign key (blog_id) references blogs(blog_id) ON DELETE CASCADE,
    primary key(blog_id, user_id)
);
insert into `blog_votes` values
(1, 2, 'up'),
(1, 3, 'up'),
(2, 3, 'up');

create table `comments` (
    `comment_id` bigint primary key not null,
	`blog_id` bigint not null, 
    `user_id` bigint not null,
    `content` varchar(50),
    `date` timestamp,
    `votes` int,
    foreign key (user_id) references users(id) ON DELETE CASCADE,
    foreign key (blog_id) references blogs(blog_id) ON DELETE CASCADE
);
insert into `comments` values
(1, 1, 2, 'hello', null, 1);

create table `comment_votes` (
	`comment_id` bigint not null,
    `user_id` bigint not null,
    `type` enum("up", "down") not null,
    foreign key (user_id) references users(id) ON DELETE CASCADE,
    foreign key (comment_id) references comments(comment_id) ON DELETE CASCADE,
    primary key(comment_id, user_id)
);
insert into `comment_votes` values
(1, 1, 'up');

create table `bookmarks` (
    `user_id` bigint not null,
	`blog_id` bigint not null, 
	foreign key (user_id) references users(id) ON DELETE CASCADE,
    foreign key (blog_id) references blogs(blog_id) ON DELETE CASCADE,
    primary key(blog_id, user_id)
);
insert into `bookmarks` values
(2, 1);

create table `followers`(
	`user_id` bigint not null,
	`follower_id` bigint not null, 
	foreign key (user_id) references users(id) ON DELETE CASCADE,
    foreign key (follower_id) references users(id) ON DELETE CASCADE,
    primary key(user_id, follower_id)
);
insert into `followers` values
(1, 2);
