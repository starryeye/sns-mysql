package dev.practice.snsmysql.domain.post.repository.custom;

import dev.practice.snsmysql.domain.post.entity.Timeline;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TimelineRepositoryCustomImpl implements TimelineRepositoryCustom{

    private final EntityManager entityManager;

    @Override
    public void bulkInsert(List<Timeline> timelines) {
        //TODO : bulk insert
    }
}
