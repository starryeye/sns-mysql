package dev.practice.snsmysql.application.usecase;

import dev.practice.snsmysql.domain.follow.dto.FollowDto;
import dev.practice.snsmysql.domain.follow.service.FollowReadService;
import dev.practice.snsmysql.domain.post.dto.PostCommand;
import dev.practice.snsmysql.domain.post.service.PostWriteService;
import dev.practice.snsmysql.domain.post.service.TimelineWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePostUsecase {

    private final PostWriteService postWriteService;
    private final FollowReadService followReadService;
    private final TimelineWriteService timelineWriteService;

    public Long execute(PostCommand command) {
        /**
         * Fan Out On Write 방식의 Write(게시물 등록) 과정
         *
         * 1. 게시물 등록
         * 2. Timeline Table 에 PostId, FromMemberId(나를 팔로우한 회원 id) 를 등록
         */
        var postId = postWriteService.create(command);

        var fromMemberIds = followReadService.getFollowerList(command.memberId()).stream()
                .map(FollowDto::fromMemberId)
                .toList();

        timelineWriteService.deliveryToTimeline(postId, fromMemberIds);

        return postId;
    }
}
