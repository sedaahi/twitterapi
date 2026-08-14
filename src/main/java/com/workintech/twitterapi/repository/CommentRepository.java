package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Bir tweet'e ait yorumları eski -> yeni sıralı getirir.
    List<Comment> findByTweetIdOrderByCreatedAtAsc(Long tweetId);

    // Bir kullanıcının yaptığı yorumları yeni -> eski sıralı getirir.
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByTweetId(Long tweetId);

    // Tweet silinirken o tweet'e ait yorumları topluca siler.
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.tweet.id = :tweetId")
    void deleteByTweetId(@Param("tweetId") Long tweetId);
}