package dev.practice.snsmysql.domain.post.repository.custom.impl;

import dev.practice.snsmysql.domain.post.entity.Timeline;
import dev.practice.snsmysql.domain.post.repository.custom.TimelineRepositoryCustomWithJdbc;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;

@RequiredArgsConstructor
public class TimelineRepositoryCustomWithJdbcImpl implements TimelineRepositoryCustomWithJdbc {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String TABLE_NAME = "timeline";

    @Override
    public void bulkInsert(List<Timeline> timelineList) {

        var sql = String.format(
                """
                INSERT INTO %s (member_id, post_id, created_at)
                VALUES (:memberId, :postId, :createdAt)
                """, TABLE_NAME
        );

        SqlParameterSource[] parameterSources = timelineList.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);
    }
}
