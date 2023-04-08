package dev.practice.snsmysql.application.usecase;

import dev.practice.snsmysql.domain.follow.dto.FollowDto;
import dev.practice.snsmysql.domain.follow.service.FollowReadService;
import dev.practice.snsmysql.domain.post.dto.PostDto;
import dev.practice.snsmysql.domain.post.service.PostReadService;
import dev.practice.snsmysql.util.CursorRequest;
import dev.practice.snsmysql.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GetTimelinePostsUsecase {

    private final FollowReadService followReadService;
    private final PostReadService postReadService;

    public PageCursor<PostDto> execute(Long memberId, CursorRequest request) {
        /**
         * 1. memberId 로 팔로우 회원들을 조회
         * 2. 팔로우 회원들의 게시물을 조회
         */

        var followList = followReadService.getFollowList(memberId);

        var toMemberIdList = followList.stream()
                .map(FollowDto::toMemberId)
                .toList();

        return postReadService.getPosts(toMemberIdList, request);
    }
}
