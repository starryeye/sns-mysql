package dev.practice.snsmysql.domain.post.repository.redis;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class PostRedisDataStoreCustomImpl implements PostRedisDataStoreCustom{

    private final HashOperations<String, String, Long> hashOperations;

    public PostRedisDataStoreCustomImpl(RedisTemplate<String, Object> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }


    @Override
    public void postLikeIncrement(String postId, Long delta) {

        String key = String.format(PostRedisHashes.KEY, postId);
        String field = PostRedisHashes.LIKE_COUNT;

        hashOperations.increment(key, field, delta);
    }
}
