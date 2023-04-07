package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private static final String TABLE_NAME = "post";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<DailyPostCount> DAILY_POST_COUNT_MAPPER =
            (rs, rowNum) -> new DailyPostCount(
                    rs.getLong("memberId"),
                    rs.getObject("createdDate", LocalDate.class),
                    rs.getLong("count")
            );

    public List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request) {
         var sql = String.format(
                 """
                 SELECT memberId, createdDate, count(id) as count
                 FROM %s
                 WHERE memberId = :memberId and createdDate between :firstDate and :lastDate
                 GROUP BY memberId, createdDate
                 """, TABLE_NAME
         ); //language=MySQL

        var params = new BeanPropertySqlParameterSource(request);

        return namedParameterJdbcTemplate.query(sql, params, DAILY_POST_COUNT_MAPPER);
    }

    public Post save(Post post) {
        if (post.getId() == null) {
            return insert(post);
        }

        throw new UnsupportedOperationException("Post 는 update 를 지원하지 않습니다.");
    }

    private Post insert(Post post) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");

        SqlParameterSource param = new BeanPropertySqlParameterSource(post);

        var id = insert.executeAndReturnKey(param).longValue();

        return Post.builder()
                .id(id)
                .memberId(post.getMemberId())
                .content(post.getContents())
                .createdDate(post.getCreatedDate())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
