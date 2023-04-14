package dev.practice.snsmysql.application.usecase;

import dev.practice.snsmysql.domain.member.service.MemberReadService;
import dev.practice.snsmysql.domain.post.service.PostLikeWriteService;
import dev.practice.snsmysql.domain.post.service.PostReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePostLikeUsecase {

    private final PostLikeWriteService postLikeWriteService;
    private final MemberReadService memberReadService;
    private final PostReadService postReadService;

    public void execute(Long postId, Long memberId) {

        var memberDto = memberReadService.getMember(memberId);
        var postDto = postReadService.getPost(postId);

        postLikeWriteService.create(postDto, memberDto);
    }
}
