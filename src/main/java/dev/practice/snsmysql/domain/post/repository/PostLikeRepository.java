package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.entity.PostLike;
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
public class PostLikeRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String TABLE_NAME = "PostLike";

    private static final RowMapper<Timeline> ROW_MAPPER =
            (rs, rowNum) -> Timeline.builder()
                    .id(rs.getLong("id"))
                    .memberId(rs.getLong("memberId"))
                    .postId(rs.getLong("postId"))
                    .createdAt(rs.getObject("createdAt", LocalDateTime.class))
                    .build();

    public PostLike save(PostLike postLike) {
        if(postLike.getId() == null) {
            return insert(postLike);
        }

        throw new UnsupportedOperationException("Timeline 는 update 를 지원하지 않습니다.");
    }

    public PostLike insert(PostLike postLike) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");

        var params = new BeanPropertySqlParameterSource(postLike);

        var id = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        return PostLike.builder()
                .id(id)
                .memberId(postLike.getMemberId())
                .postId(postLike.getPostId())
                .createdAt(postLike.getCreatedAt())
                .build();
    }
}
