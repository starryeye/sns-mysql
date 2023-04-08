package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private static final String TABLE_NAME = "post";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Post> ROW_MAPPER =
            (rs, rowNum) -> Post.builder()
                    .id(rs.getLong("id"))
                    .memberId(rs.getLong("memberId"))
                    .content(rs.getString("contents"))
                    .createdDate(rs.getObject("createdDate", LocalDate.class))
                    .createdAt(rs.getObject("createdAt", LocalDateTime.class))
                    .build();

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

    public Page<Post> findAllByMemberId(Long memberId, Pageable pageable) {

        var sql = String.format(
                """
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                LIMIT :size
                OFFSET :offset
                """, TABLE_NAME
        ); //language=MySQL

        var params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        var posts = namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);

        return new PageImpl(posts, pageable, getCount(memberId));
    }

    private Long getCount(Long memberId) {
        var sql = String.format(
                """
                SELECT count(id)
                FROM %s
                WHERE memberId = :memberId
                """, TABLE_NAME
        );

        var param = new MapSqlParameterSource()
                .addValue("memberId", memberId);

        return namedParameterJdbcTemplate.queryForObject(sql, param, Long.class);
    }

    public Post save(Post post) {
        if (post.getId() == null) {
            return insert(post);
        }

        throw new UnsupportedOperationException("Post 는 update 를 지원하지 않습니다.");
    }

    /**
     * Test 용도인 벌크 데이터 저장 기능
     *
     * 기존에 insert 메서드는 여러번 호출하면 INSERT 쿼리가 하나하나 실행된다.
     * 이를 bulk 로 하나의 쿼리로 실행하도록 만든 메서드이다.
     *
     * SqlParameterSource[] 로 여러개의 데이터를 바인딩 할 수 있다.
     *
     * VALUES (~,~,~,~), (~,~,~,~), (~,~,~,~) 와 같은 형태로 쿼리가 실행된다.
     *
     * JPA 에서는 @BatchSize 를 사용하면 된다.
     * 하지만, Auto Increment 를 사용하는 경우에는 @BatchSize 를 사용하여도 쿼리가 여러번 실행된다.
     */
    public void bulkInsert(List<Post> posts) {
        var sql = String.format(
                """
                INSERT INTO %s (memberId, contents, createdDate, createdAt)
                VALUES (:memberId, :contents, :createdDate, :createdAt)
                """, TABLE_NAME
        ); //language=MySQL

        SqlParameterSource[] params = posts.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, params);
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
