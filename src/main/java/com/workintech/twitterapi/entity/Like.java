package com.workintech.twitterapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tweet_like",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "tweet_id"})} //aynı tweet'i birden fazla kez beğenmesini engellemek
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;
}