package dev.practice.snsmysql.domain.post.service;

import dev.practice.snsmysql.domain.post.entity.Timeline;
import dev.practice.snsmysql.domain.post.repository.jdbc.TimelineRepositoryByJdbc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimelineWriteService {

    //Bulk insert 는 jpa 에서 @GeneratedValue(strategy = GenerationType.IDENTITY) 를 사용하는 경우..
    //한방 쿼리가 불가능 하여 jdbcTemplate 와 같이 native query 를 사용해야한다..
    //TODO : JPA 의 @Query(native = true) 로 해결 검토
    private final TimelineRepositoryByJdbc timelineRepository;

    public void deliveryToTimeline(Long postId, List<Long> fromMemberIds) {
        /**
         * fan out on write
         * 게시물을 작성한 회원을 팔로우한 회원들에게 타임라인에 게시물을 배달(Timeline Table 에 저장)한다.
         */
        var timelineList = fromMemberIds.stream()
                .map(toMemberId -> Timeline.builder().memberId(toMemberId).postId(postId).build())
                .toList();

        timelineRepository.bulkInsert(timelineList);
    }
}
