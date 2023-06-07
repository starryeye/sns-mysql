package dev.practice.snsmysql.domain.post.repository.redis;

public interface PostRedisDataStoreCustom {

    void postLikeIncrement(String postId, Long delta);
}
