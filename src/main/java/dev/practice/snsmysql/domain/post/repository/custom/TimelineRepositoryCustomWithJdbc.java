package dev.practice.snsmysql.domain.post.repository.custom;

import dev.practice.snsmysql.domain.post.entity.Timeline;

import java.util.List;

public interface TimelineRepositoryCustomWithJdbc {

    void bulkInsert(List<Timeline> timelineList);
}
