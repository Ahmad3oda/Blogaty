package com.blog.demo.repository;

import com.blog.demo.entity.Follower;
import com.blog.demo.entity.FollowerID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

    @Query("SELECT f.receiver.id from Follower f where f.actor.id = :userId")
    List<Long> findFollowersIdByUserId(int userId);
    Follower findByReceiver_IdAndActor_Id(int id_userId, int id_followerId);

    Integer countByReceiver_Id(int receiver_id);
    Integer countByActor_Id(int actor_id);

    @Query("SELECT f.actor.id from Follower f where f.receiver.id = :userId")
    List<Long> findFollowingsIdByUserId(int userId);
}
