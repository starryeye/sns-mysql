package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.entity.Timeline;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimelineRepository extends JpaRepository<Timeline, Long> {

    //orderBy id, limit 설정 필요
    List<Timeline> findAllByMemberId(Long memberId, Pageable pageable);

    //orderBy id, limit 설정 필요
    List<Timeline> findAllByIdLessThanAndMemberId(Long id, Long memberId, Pageable pageable);
}
