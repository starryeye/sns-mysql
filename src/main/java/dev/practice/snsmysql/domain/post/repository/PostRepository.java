package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.custom.PostRepositoryCustom;
import dev.practice.snsmysql.domain.post.repository.custom.PostRepositoryCustomWithJdbc;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom, PostRepositoryCustomWithJdbc {

    /**
     * Group by 를 하는 순간 집계 함수에 대한 컬럼이 추가 되므로..
     * entity 를 온전히 받을 수 없다.
     * JPQL 을 사용하거나 Projection 을 사용해야한다.
     * 여기선 Jpql 로 해보겠다..
     * TODO : Projection 시도
     * - Projection 은 대상 DTO 클래스를 interface 로 선언하고 service 에서 추가적으로 리턴된 proxy 객체를 실제 객체로 변환 과정이 필요하다.
     * - 결국.. 서비스에서 한번 더 순회하는 것..
     *
     * SQL:
     * SELECT member_id, created_date, count(id) as count
     * FROM %s
     * WHERE member_id = :memberId and created_date between :firstDate and :lastDate
     * GROUP BY member_id, created_date
     *
     * JPQL : custom 에도 구현
     */
    @Query("""
            select new dev.practice.snsmysql.domain.post.dto.DailyPostCount(p.memberId, p.createdDate, count(p.id))
            from Post p
            where
                p.memberId = :#{#dto.memberId()} and
                p.createdDate between :#{#dto.firstDate()} and :#{#dto.lastDate()}
            group by p.memberId, p.createdDate
            """)
    List<DailyPostCount> countByMemberIdAndCreatedDateBetween(@Param("dto")DailyPostCountRequest request);

    /**
     * SQL(따로 Page 로 변환):
     * SELECT *
     * FROM %s
     * WHERE member_id = :memberId
     * ORDER BY %s
     * LIMIT :size
     * OFFSET :offset
     */
    @Query(countQuery = "select count(p.id) from Post p where p.memberId = :memberId")
    Page<Post> findAllByMemberId(Long memberId, Pageable pageable);

    /**
     * SQL :
     * SELECT *
     * FROM %s
     * WHERE id = :id
     * FOR UPDATE
     *
     * @Lock 을 통해서 비관적 락 적용 된다.
     * Query Method 로 진행하려니까.. 메서드 이름이 이상하게 인식됨..
     * -> findByIdUsingPessimisticWriteLock 에서 Using 이하 무시가 안됨..(Using, For, With 모두 동일한 에러)
     * 그래서, @Query 로 진행
     * -> TODO : 질문.. 해볼 것 (+ merge 관련 dirty check)
     */
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select p from Post p where p.id = :id")
    Optional<Post> findByIdUsingPessimisticWriteLock(@Param("id") Long id);

    /**
     * SQL :
     * SELECT *
     * FROM %s
     * WHERE id in (:ids)
     *
     * JPQL :
     * select p from Post p where p.id in :ids
     */
    List<Post> findAllByIdIn(List<Long> ids);

    /**
     * Cursor 기반 페이지네이션, totalCount 쿼리 없음
     *
     * jdbcTemplate : List<Post> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size)
     *
     * SQL :
     * SELECT *
     * FROM %s
     * WHERE member_id = :memberId
     * ORDER BY id DESC
     * LIMIT :size
     *
     * JPQL :
     * custom 에서 구현
     *
     * Query Method :
     * findTopXXByMemberIdOrderByIdDesc ..
     * -> XX(limit)를 변수로 받을 수 없어서 Custom..
     */
//    List<Post> findTopXXByMemberIdOrderByIdDesc(Long memberId);

    /**
     * Cursor 기반 페이지네이션, totalCount 쿼리 없음
     *
     * jdbcTemplate : List<Post> findAllByLessThanIdAndMemberIdAndOrderByIdDesc(Long id, Long memberId, int size)
     *
     * SQL :
     * SELECT *
     * FROM %s
     * WHERE member_id = :memberId and id < :id
     * ORDER BY id DESC
     * LIMIT :size
     *
     * JPQL :
     * custom 에서 구현
     *
     * Query Method :
     * findTopXXByMemberIdOrderByIdDesc ..
     * -> XX(limit)를 변수로 받을 수 없어서 Custom..
     */
//    List<Post> findTopXXByMemberIdAndIdLessThanOrderByIdDesc(Long memberId, Long id, int size);

    /**
     * Cursor 기반 페이지네이션, totalCount 쿼리 없음
     *
     * jdbcTemplate : List<Post> findAllByInMemberIdsAndOrderByIdDesc(List<Long> memberIds, int size)
     *
     * SQL :
     * SELECT *
     * FROM %s
     * WHERE member_id in (:memberIds)
     * ORDER BY id DESC
     * LIMIT :size
     *
     * JPQL, Query Method : 상동
     */
//    List<Post> findTopXXByMemberIdInOrderByIdDesc(List<Long> memberIds);

    /**
     * Cursor 기반 페이지네이션, totalCount 쿼리 없음
     *
     * jdbcTemplate : List<Post> findAllByLessThanIdAndInMemberIdsAndOrderByIdDesc(Long id, List<Long> memberIds, int size)
     *
     * SQL :
     * SELECT *
     * FROM %s
     * WHERE member_id in (:memberIds) and id < :id
     * ORDER BY id DESC
     * LIMIT :size
     *
     * JPQL, Query Method : 상동
     */
//    List<Post> findTopXXByMemberIdInAndIdLessThanOrderByIdDesc(List<Long> memberIds);

    /**
     * bulk insert
     * @Query(nativeQuery=true, value="~") 로 시도 해보려 했으나..
     * native query 라서 Post 프로퍼티가 낱개로 쿼리 문에 존재하는데 파라미터로는 List<Post> 형식으로 들어오기 때문에..
     * ":#{#}" 로 해결해보려 했으나 문법이 안맞음..
     * JPQL 로 해결하려면 MySQL 에서는 IDENTITY 전략이므로 bulk insert 안됨..
     * -> TODO : JdbcTemplate 을 사용하는 방법 말고 @Query(nativeQuery=true) 로 안되는지 더 찾아보기..
     */

    /**
     * Optimistic Lock, Update
     * -> @Version 만 Entity 에 적용해도 적용된다. (LockModeType.NONE)
     * TODO: 모든 LockModeType 학습
     */
}
