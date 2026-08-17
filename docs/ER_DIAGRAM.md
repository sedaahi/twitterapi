# Database ER Diagram

```mermaid
erDiagram
    APP_USER {
        BIGINT id PK
        VARCHAR username UK
        VARCHAR email UK
        VARCHAR password
    }

    TWEET {
        BIGINT id PK
        VARCHAR content
        TIMESTAMP created_at
        BIGINT user_id FK
    }

    COMMENT {
        BIGINT id PK
        VARCHAR content
        TIMESTAMP created_at
        BIGINT user_id FK
        BIGINT tweet_id FK
    }

    TWEET_LIKE {
        BIGINT id PK
        BIGINT user_id FK
        BIGINT tweet_id FK
    }

    RETWEET {
        BIGINT id PK
        TIMESTAMP created_at
        BIGINT user_id FK
        BIGINT tweet_id FK
    }

    APP_USER ||--o{ TWEET : creates
    APP_USER ||--o{ COMMENT : writes
    TWEET ||--o{ COMMENT : has

    APP_USER ||--o{ TWEET_LIKE : likes
    TWEET ||--o{ TWEET_LIKE : receives

    APP_USER ||--o{ RETWEET : retweets
    TWEET ||--o{ RETWEET : receives
```

## Constraints

- `app_user.username` is unique.
- `app_user.email` is unique.
- `tweet_like` has a unique constraint on `(user_id, tweet_id)`.
- `retweet` has a unique constraint on `(user_id, tweet_id)`.