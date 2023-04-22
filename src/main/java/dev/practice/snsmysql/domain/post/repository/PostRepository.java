package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
