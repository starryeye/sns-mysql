package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
}
