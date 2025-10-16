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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("receiverId")
    @JoinColumn(name = "user_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("actorId")
    @JoinColumn(name = "follower_id", nullable = false)
    private User actor;

    public Follower(User receiver, User actor) {
        this.receiver = receiver;
        this.actor = actor;
        this.id = new FollowerID(receiver.getId(), actor.getId());
    }

}
