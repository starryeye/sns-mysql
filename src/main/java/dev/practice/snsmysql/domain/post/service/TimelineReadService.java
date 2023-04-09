package dev.practice.snsmysql.domain.post.service;

import dev.practice.snsmysql.domain.post.entity.Timeline;
import dev.practice.snsmysql.domain.post.repository.TimelineRepository;
import dev.practice.snsmysql.util.CursorRequest;
import dev.practice.snsmysql.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * fan out on write 방식을 위한 타임라인 조회 서비스
 *
 * TODO: Timeline 반환 TimelineDto 로 변경
 */
@Service
@RequiredArgsConstructor
public class TimelineReadService {

    private final TimelineRepository timelineRepository;

    public PageCursor<Timeline> getTimelineList(Long memberId, CursorRequest request) {
        var timelineList = findAllBy(memberId, request);

        var lastKey = getLastKey(timelineList);

        return new PageCursor<>(request.next(lastKey), timelineList);
    }

    private List<Timeline> findAllBy(Long memberId, CursorRequest request) {

        if(request.hasKey()) {
            return timelineRepository.findAllByLessThanIdAndMemberIdAndOrderByIdDesc(request.key(), memberId, request.size());
        }

        return timelineRepository.findAllByMemberIdAndOrderByIdDesc(memberId, request.size());
    }

    private static long getLastKey(List<Timeline> timelines) {
        return timelines.stream()
                .mapToLong(Timeline::getId)
                .min()
                .orElse(CursorRequest.NONE_KEY);
    }

}
