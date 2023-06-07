package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.repository.redis.PostRedisDataStoreCustom;
import dev.practice.snsmysql.domain.post.repository.redis.PostRedisHashes;
import org.springframework.data.repository.CrudRepository;

public interface PostRedisDataStore extends CrudRepository<PostRedisHashes, String>, PostRedisDataStoreCustom {
}
