package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.custom.PostRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

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
}
