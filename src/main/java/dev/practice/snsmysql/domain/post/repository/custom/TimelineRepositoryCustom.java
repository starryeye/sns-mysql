package dev.practice.snsmysql.domain.post.repository.custom;

import dev.practice.snsmysql.domain.post.entity.Timeline;
import org.springframework.data.domain.Sort;

import java.util.List;

//TODO : Slice 도입 후 삭제 시도
public interface TimelineRepositoryCustom {

    /**
     * SQL:
     * SELECT id, memberId, postId, createdAt
     * FROM %s
     * WHERE memberId = :memberId
     * ORDER BY id DESC
     * LIMIT :size
     *
     * JPQL:
     * String jpql = "select t from Timeline t where t.memberId = :memberId order by t.id desc";
     *
     * return em.createQuery(jpql, Timeline.class)
     *              .setParameter("memberId", memberId)
     *              .setMaxResults(size)
     *              .getResultList();
     *
     * 참고로 Sort 를 Jpql 로 풀려면 복잡한 코드가 들어가야한다.
     * 기존에도 따로 변수로 받지 않았으니(ORDER BY id DESC) 여기서도 동일하게 하겠다.
     */
    List<Timeline> findAllByMemberIdWithLimit(Long memberId, int size);

    /**
     * SQL:
     * SELECT id, memberId, postId, createdAt
     * FROM %s
     * WHERE id < :id and memberId = :memberId
     * ORDER BY id DESC
     * LIMIT :size
     *
     * JPQL:
     * String jpql = "select t from Timeline t where t.id < :id and t.memberId = :memberId order by t.id desc";
     *
     * return em.createQuery(jpql, Timeline.class)
     *              .setParameter("id", id)
     *              .setParameter("memberId", memberId)
     *              .setMaxResults(size)
     *              .getResultList();
     *
     * 참고로 Sort 를 Jpql 로 풀려면 복잡한 코드가 들어가야한다.
     * 기존에도 따로 변수로 받지 않았으니(ORDER BY id DESC) 여기서도 동일하게 하겠다.
     */
    List<Timeline> findAllByIdLessThanAndMemberIdWithLimit(Long id, Long memberId, int size);
}
