package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.RetweetRequest;
import com.workintech.twitterapi.entity.Retweet;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.TwitterException;
import com.workintech.twitterapi.repository.RetweetRepository;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RetweetServiceImpl implements RetweetService {

    private final RetweetRepository retweetRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public RetweetServiceImpl(
            RetweetRepository retweetRepository,
            TweetRepository tweetRepository,
            UserRepository userRepository
    ) {
        this.retweetRepository = retweetRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void retweet(RetweetRequest request, String email) {

        User user = findUserByEmail(email);
        Tweet tweet = findTweetById(request.tweetId()); ///Hangi tweet retweet edilecek?

        boolean alreadyRetweeted =
                retweetRepository.existsByUserIdAndTweetId(
                        user.getId(),
                        tweet.getId()
                );

        if (alreadyRetweeted) {
            throw new TwitterException(
                    "Bu tweet zaten retweet edilmiş.",
                    HttpStatus.BAD_REQUEST
            );
        }

        Retweet retweet = new Retweet();
        retweet.setUser(user);
        retweet.setTweet(tweet);

        retweetRepository.save(retweet);
    }

    /**
     * Tweet
     * id = 5
     *
     * Retweet
     * id = 12
     * tweet_id = 5
     * user_id = 2
     * DELETE /retweet/12 ==> 5 değil!!!
     */
    @Override
    public void deleteRetweet(Long id, String email) { ///Retweet'in kendi id'si

        Retweet retweet = retweetRepository.findById(id)
                .orElseThrow(() ->
                        new TwitterException(
                                "Retweet bulunamadı.",
                                HttpStatus.NOT_FOUND
                        )
                );

        if (!retweet.getUser().getEmail().equals(email)) {
            throw new TwitterException(
                    "Bu retweet'i silme yetkiniz yok.",
                    HttpStatus.FORBIDDEN
            );
        }

        retweetRepository.delete(retweet);
    }

    private User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new TwitterException(
                                "Kullanıcı bulunamadı.",
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    private Tweet findTweetById(Long id) {

        return tweetRepository.findById(id)
                .orElseThrow(() ->
                        new TwitterException(
                                "Tweet bulunamadı.",
                                HttpStatus.NOT_FOUND
                        )
                );
    }
}