package dev.practice.snsmysql.domain.post.repository.custom;

import dev.practice.snsmysql.domain.post.entity.Post;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public void bulkInsert(List<Post> posts) {
        //TODO : bulk insert

//        var sql = String.format(
//                """
//                INSERT INTO %s (memberId, contents, createdDate, createdAt)
//                VALUES (:memberId, :contents, :createdDate, :createdAt)
//                """, TABLE_NAME
//        ); //language=MySQL
    }
}
