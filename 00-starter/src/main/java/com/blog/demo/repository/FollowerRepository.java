package com.blog.demo.repository;

import com.blog.demo.entity.Follower;
import com.blog.demo.entity.FollowerID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, FollowerID> {

    // Returns IDs of users who follow the given userId (i.e., the actors)
    @Query("SELECT f.actor.id from Follower f where f.receiver.id = :userId")
    List<Long> findFollowersIdByUserId(Long userId);

    Follower findByReceiver_IdAndActor_Id(Long id_userId, Long id_followerId);

    Integer countByReceiver_Id(Long receiver_id);
    Integer countByActor_Id(Long actor_id);

    // Returns IDs of users that the given userId follows (i.e., the receivers)
    @Query("SELECT f.receiver.id from Follower f where f.actor.id = :userId")
    List<Long> findFollowingsIdByUserId(Long userId);
}
