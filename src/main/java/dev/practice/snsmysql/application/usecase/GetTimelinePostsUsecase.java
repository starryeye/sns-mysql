package dev.practice.snsmysql.application.usecase;

import dev.practice.snsmysql.domain.follow.dto.FollowDto;
import dev.practice.snsmysql.domain.follow.service.FollowReadService;
import dev.practice.snsmysql.domain.post.dto.PostDto;
import dev.practice.snsmysql.domain.post.dto.TimelineDto;
import dev.practice.snsmysql.domain.post.service.PostReadService;
import dev.practice.snsmysql.domain.post.service.TimelineReadService;
import dev.practice.snsmysql.util.CursorRequest;
import dev.practice.snsmysql.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GetTimelinePostsUsecase {

    //fan out on read
    private final FollowReadService followReadService;
    private final PostReadService postReadService;

    //fan out on write
    private final TimelineReadService timelineReadService;


    public PageCursor<PostDto> execute(Long memberId, CursorRequest request) {
        /**
         * Fan Out On Read
         * DB 관점에서 보면, Follow 테이블에서 Post 테이블로 간다.
         *
         * 1. memberId 로 팔로우 회원들을 조회
         * 2. 팔로우 회원들의 게시물을 조회
         */

        var followList = followReadService.getFollowingList(memberId);

        var toMemberIdList = followList.stream()
                .map(FollowDto::toMemberId)
                .toList();

        return postReadService.getPosts(toMemberIdList, request);
    }

    public PageCursor<PostDto> executeByTimeline(Long memberId, CursorRequest request) {
        /**
         * Fan Out On Write 방식에서 Read 부분
         * DB 관점에서 보면, Timeline 테이블에서 Post 테이블로 간다.
         *
         * 1. Timeline 조회
         * 2. 1번에 해당하는 게시물을 조회한다.
         *
         * 1, 2 번을 조인으로 처리해도 되지만 1번 조회 후, 2번에서 in query 를 사용하겠다.
         */

        var pagedTimelineList = timelineReadService.getTimelineList(memberId, request);

        var postIdList = pagedTimelineList.contents().stream()
                .map(TimelineDto::postId)
                .toList();

        var postList = postReadService.getPosts(postIdList);

        return new PageCursor<>(pagedTimelineList.cursorRequest(), postList);
    }
}
