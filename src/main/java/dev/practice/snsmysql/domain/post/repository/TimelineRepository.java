package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.entity.Timeline;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TimelineRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String TABLE_NAME = "timeline";

    private static final RowMapper<Timeline> ROW_MAPPER =
            (rs, rowNum) -> Timeline.builder()
                    .id(rs.getLong("id"))
                    .memberId(rs.getLong("memberId"))
                    .postId(rs.getLong("postId"))
                    .createdAt(rs.getObject("createdAt", LocalDateTime.class))
                    .build();

    //Cursor 기반 페이징 처리 방식, 최초 용도
    public List<Timeline> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size) {
        var sql = String.format(
                """
                SELECT id, memberId, postId, createdAt
                FROM %s
                WHERE memberId = :memberId
                ORDER BY id DESC
                LIMIT :size
                """, TABLE_NAME
        ); //language=MySQL

        var params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", size);

        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    //Cursor 기반 페이징 처리 방식, 최초가 아닐때는 key 값(id)을 이용하여 조회
    public List<Timeline> findAllByLessThanIdAndMemberIdAndOrderByIdDesc(Long id, Long memberId, int size) {
        var sql = String.format(
                """
                SELECT id, memberId, postId, createdAt
                FROM %s
                WHERE id < :id and memberId = :memberId
                ORDER BY id DESC
                LIMIT :size
                """, TABLE_NAME
        ); //language=MySQL

        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("memberId", memberId)
                .addValue("size", size);

        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    public Timeline save(Timeline timeline) {
        if(timeline.getId() == null) {
            return insert(timeline);
        }

        throw new UnsupportedOperationException("Timeline 는 update 를 지원하지 않습니다.");
    }

    public Timeline insert(Timeline timeline) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");

        var params = new BeanPropertySqlParameterSource(timeline);

        var id = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        return Timeline.builder()
                .id(id)
                .memberId(timeline.getMemberId())
                .postId(timeline.getPostId())
                .createdAt(timeline.getCreatedAt())
                .build();
    }

    public void bulkInsert(List<Timeline> timelineList) {
        var sql = String.format(
                """
                INSERT INTO %s (memberId, postId, createdAt)
                VALUES (:memberId, :postId, :createdAt)
                """, TABLE_NAME
        ); //language=MySQL

        SqlParameterSource[] params = timelineList.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }
}
