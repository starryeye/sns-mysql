package dev.practice.snsmysql.domain.post.repository.custom;

import dev.practice.snsmysql.domain.post.entity.Timeline;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TimelineRepositoryCustomImpl implements TimelineRepositoryCustom{

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Timeline> findAllByMemberIdWithLimit(Long memberId, int size) {
        return entityManager.createQuery("select t from Timeline t where t.memberId = :memberId order by t.id desc", Timeline.class)
                .setParameter("memberId", memberId)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public List<Timeline> findAllByIdLessThanAndMemberIdWithLimit(Long id, Long memberId, int size) {
        return entityManager.createQuery("select t from Timeline t where t.id < :id and t.memberId = :memberId order by t.id desc", Timeline.class)
                .setParameter("id", id)
                .setParameter("memberId", memberId)
                .setMaxResults(size)
                .getResultList();
    }
}