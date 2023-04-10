package dev.practice.snsmysql.application.usecase;

import dev.practice.snsmysql.domain.follow.dto.FollowDto;
import dev.practice.snsmysql.domain.follow.service.FollowReadService;
import dev.practice.snsmysql.domain.post.dto.PostCommand;
import dev.practice.snsmysql.domain.post.service.PostWriteService;
import dev.practice.snsmysql.domain.post.service.TimelineWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatePostUsecase {

    private final PostWriteService postWriteService;
    private final FollowReadService followReadService;
    private final TimelineWriteService timelineWriteService;

//    @Transactional
    public Long execute(PostCommand command) {
        /**
         * Fan Out On Write 방식의 Write(게시물 등록) 과정
         *
         * 1. 게시물 등록
         * 2. 게시물을 등록한 회원을 팔로우한 회원들을 조회
         * 3. Timeline Table 에 PostId, FromMemberId(나를 팔로우한 회원 id) 를 등록
         *
         * 2, 3 번 과정은 Fan Out On Write 방식으로 처리된다.
         * 게시물 작성자를 팔로우한 회원이 많을 수록 급격하게 성능 저하가 발생한다.
         *
         * 그래서 1, 2, 3 번 전 과정을 하나의 트랜잭션으로 묶는 것은 팔로우 수가 많을 때..
         * 시간이 오래걸리므로 고민해봐야 한다.
         *
         * 비동기 처리를 하던가 timeline table 을 캐시 처리하면 성능이 향상될 수 있다.
         */
        var postId = postWriteService.create(command);

        var fromMemberIds = followReadService.getFollowerList(command.memberId()).stream()
                .map(FollowDto::fromMemberId)
                .toList();

        timelineWriteService.deliveryToTimeline(postId, fromMemberIds);

        return postId;
    }
}
