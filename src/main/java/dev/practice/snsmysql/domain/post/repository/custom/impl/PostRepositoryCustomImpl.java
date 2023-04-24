package dev.practice.snsmysql.domain.post.repository.custom.impl;

import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.custom.PostRepositoryCustom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<DailyPostCount> countByMemberIdAndCreatedDateByCustomJpql(DailyPostCountRequest request) {
        return entityManager.createQuery(
                "select new dev.practice.snsmysql.domain.post.dto.DailyPostCount(p.memberId, p.createdDate, count(p.id)) " +
                "from Post p " +
                "where p.memberId = :memberId and p.createdDate between :firstDate and :lastDate " +
                "group by p.memberId, p.createdDate")
                .setParameter("memberId", request.memberId())
                .setParameter("firstDate", request.firstDate())
                .setParameter("lastDate", request.lastDate())
                .getResultList();
    }

    @Override
    public List<Post> findAllByMemberIdOrderByIdDescWithLimit(Long memberId, int size) {
        return entityManager.createQuery("select p from Post p where p.memberId = :memberId order by p.id desc", Post.class)
                .setParameter("memberId", memberId)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public List<Post> findAllByMemberIdAndIdLessThanOrderByIdDescWithLimit(Long id, Long memberId, int size) {
        return entityManager.createQuery("select p from Post p where p.memberId = :memberId and p.id < :id order by p.id desc", Post.class)
                .setParameter("memberId", memberId)
                .setParameter("id", id)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public List<Post> findAllByMemberIdInOrderByIdDescWithLimit(List<Long> memberIds, int size) {
        return entityManager.createQuery(
                """
                        select p
                        from Post p
                        where p.memberId in :memberIds
                        order by p.id DESC
                        """, Post.class
                )
                .setParameter("memberIds", memberIds)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public List<Post> findAllByMemberIdInAndIdLessThanOrderByIdDesWithLimit(Long id, List<Long> memberIds, int size) {
        return entityManager.createQuery(
                """
                        select p
                        from Post p
                        where p.memberId in :memberIds and p.id < :id
                        order by p.id DESC
                        """, Post.class
                )
                .setParameter("memberIds", memberIds)
                .setParameter("id", id)
                .setMaxResults(size)
                .getResultList();
    }
}
