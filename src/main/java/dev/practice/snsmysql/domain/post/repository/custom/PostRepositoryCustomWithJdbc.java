package dev.practice.snsmysql.domain.post.repository.custom;

import dev.practice.snsmysql.domain.post.entity.Post;

import java.util.List;

public interface PostRepositoryCustomWithJdbc {

    void bulkInsert(List<Post> postList);
}
