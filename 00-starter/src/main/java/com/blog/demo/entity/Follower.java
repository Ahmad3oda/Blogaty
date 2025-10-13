package com.blog.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "followers")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Follower {

    @EmbeddedId
    private FollowerID id;
}
