package com.blog.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowerID implements Serializable {
    @Column(name = "user_id", nullable = false)
    private int receiverId;

    @Column(name = "follower_id", nullable = false)
    private int actorId;
}
