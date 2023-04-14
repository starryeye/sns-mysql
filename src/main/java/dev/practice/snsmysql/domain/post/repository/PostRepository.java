package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.util.PageHelper;
import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.util.Optional;

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
                    .likeCount(rs.getLong("likeCount"))
                    .version(rs.getLong("version"))
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

    //offset 기반 페이징 처리 방식
    public Page<Post> findAllByMemberId(Long memberId, Pageable pageable) {

        var sql = String.format(
                """
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY %s
                LIMIT :size
                OFFSET :offset
                """, TABLE_NAME, PageHelper.orderBy(pageable.getSort())
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

    /**
     * 동시성 문제를 해결하기 위해서 select for update 를 한다.
     * 모든 조회에 대해서 select for update 를 하면 성능이 떨어진다.
     *
     * 여기서는 파라미터를 받아서 락 여부를 제어하겠다..
     * JPA 에서는 어노테이션을 통해서 해결할 수 있다.
     * TODO: @Transactional(readOnly = false), @Lock(LockModeType.PESSIMISTIC_WRITE) 찾아보기
     */
    public Optional<Post> findById(Long postId, Boolean requiredLock) {
        var sql = String.format(
                """
                SELECT *
                FROM %s
                WHERE id = :id
                """, TABLE_NAME
        );

        if(requiredLock) {
            sql += "FOR UPDATE";
        }

        var param = new MapSqlParameterSource()
                .addValue("id", postId);

        Post post = namedParameterJdbcTemplate.queryForObject(sql, param, ROW_MAPPER);

        return Optional.ofNullable(post);
    }

    public List<Post> findAllByInIds(List<Long> ids) {

        if(ids.isEmpty())
            return List.of();

        var sql = String.format(
                """
                SELECT *
                FROM %s
                WHERE id in (:ids)
                """, TABLE_NAME
        );

        var params = new MapSqlParameterSource()
                .addValue("ids", ids);

        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    //Cursor 기반 페이징 처리 방식, 최초 용도
    public List<Post> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size) {
        var sql = String.format(
                """
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY id DESC
                LIMIT :size
                """, TABLE_NAME
        );

        var params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", size);

        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    //Cursor 기반 페이징 처리 방식, 최초 용도, memberId list 용도 (timeline)
    public List<Post> findAllByInMemberIdsAndOrderByIdDesc(List<Long> memberIds, int size) {

        //memberIds 가 없으면 빈 리스트를 반환, list 가 아닌 케이스에서는 @PathVariable 로 받았기 때문에 필수였다.
        if(memberIds.isEmpty())
            return List.of();

        var sql = String.format(
                """
                SELECT *
                FROM %s
                WHERE memberId in (:memberIds)
                ORDER BY id DESC
                LIMIT :size
                """, TABLE_NAME
        );

        var params = new MapSqlParameterSource()
                .addValue("memberIds", memberIds)
                .addValue("size", size);

        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    //Cursor 기반 페이징 처리 방식, 최초가 아닐때는 key 값(id)을 이용하여 조회
    public List<Post> findAllByLessThanIdAndMemberIdAndOrderByIdDesc(Long id, Long memberId, int size) {
        var sql = String.format(
                """
                SELECT *
                FROM %s
                WHERE memberId = :memberId and id < :id
                ORDER BY id DESC
                LIMIT :size
                """, TABLE_NAME
        );

        var params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("id", id)
                .addValue("size", size);

        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    //Cursor 기반 페이징 처리 방식, 최초가 아닐때는 key 값을 이용하여 조회, memberId list 용도 (timeline)
    public List<Post> findAllByLessThanIdAndInMemberIdsAndOrderByIdDesc(Long id, List<Long> memberIds, int size) {

        //memberIds 가 없으면 빈 리스트를 반환, list 가 아닌 케이스에서는 @PathVariable 로 받았기 때문에 필수였다.
        if(memberIds.isEmpty())
            return List.of();

        var sql = String.format(
                """
                SELECT *
                FROM %s
                WHERE memberId in (:memberIds) and id < :id
                ORDER BY id DESC
                LIMIT :size
                """, TABLE_NAME
        );

        var params = new MapSqlParameterSource()
                .addValue("memberIds", memberIds)
                .addValue("id", id)
                .addValue("size", size);

        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    public Post save(Post post) {
        if (post.getId() == null) {
            return insert(post);
        }

//        throw new UnsupportedOperationException("Post 는 update 를 지원하지 않습니다.");
        return update(post);
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

    private Post update(Post post) {
        var sql = String.format(
                """
                UPDATE %s SET
                    memberId = :memberId,
                    contents = :contents,
                    createdDate = :createdDate,
                    likeCount = :likeCount,
                    createdAt = :createdAt,
                    version = :version + 1
                WHERE id = :id and version = :version
                """, TABLE_NAME
        );
        //optimistic locking 을 위해서 version 컬럼을 추가하고, update 시에 version 을 1 증가시킨다.
        //동시에 같은 데이터를 수정하면 version 이 동일하게 조회되고 두번째 update 는 실패한다.

        var params = new BeanPropertySqlParameterSource(post);

        var updatedCount = namedParameterJdbcTemplate.update(sql, params);

        if(updatedCount == 0) { //optimistic locking 실패, updatedCount 가 0 이면 수정한 데이터가 없다는 의미이다.
            throw new OptimisticLockingFailureException("Post 갱신 실패, 이미 수정되었습니다."); //TODO: 새로운 Exception 을 만들고 catch 하여 실패 처리를 따로 개발
        }

        return post;
    }
}
