package dev.practice.snsmysql.domain.post.repository.custom.impl;

import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.custom.PostRepositoryCustomWithJdbc;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryCustomWithJdbcImpl implements PostRepositoryCustomWithJdbc {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String TABLE_NAME = "post";

    @Override
    public void bulkInsert(List<Post> postList) {

        var sql = String.format(
                """
                INSERT INTO %s (member_id, contents, created_date, like_count, version, created_at)
                VALUES (:memberId, :contents, :createdDate, :likeCount, :version, :createdAt)
                """, TABLE_NAME
        );

        SqlParameterSource[] sqlParameterSources = postList.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, sqlParameterSources);
    }
}
