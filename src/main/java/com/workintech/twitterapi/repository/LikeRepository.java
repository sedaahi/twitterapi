package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);

    Optional<Like> findByUserIdAndTweetId(
            Long userId,
            Long tweetId
    );

    long countByTweetId(Long tweetId);

    // Kullanıcının beğendiği tweet kayıtlarını
    // en son beğenilenden eskiye doğru getirir.
    List<Like> findByUserIdOrderByIdDesc(Long userId);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.tweet.id = :tweetId")
    void deleteByTweetId(@Param("tweetId") Long tweetId);
}