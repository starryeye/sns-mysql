package dev.practice.snsmysql.domain.post.repository.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Objects;

@Getter
@RedisHash(value = "post")
public class PostRedisHashes {

    public static final String KEY = "post:%s";
    public static final String LIKE_COUNT = "likeCount";

    @Id
    private String id; //postId

    private Long likeCount;

    //.. 해당 post 의 다양한 정보들을 추가해볼 수 있다.

    @Builder
    public PostRedisHashes(String id, Long likeCount) {
        this.id = Objects.requireNonNull(id);
        this.likeCount = likeCount == null ? 0L : likeCount;
    }
}
