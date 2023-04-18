package dev.practice.snsmysql.domain.post.service;

import dev.practice.snsmysql.domain.member.dto.MemberDto;
import dev.practice.snsmysql.domain.post.dto.PostDto;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.entity.PostLike;
import dev.practice.snsmysql.domain.post.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Post like 를 위한 서비스
 *
 * TODO: 한명의 회원은 하나의 게시물에 대해 한번만 좋아요를 누를 수 있는 기능은.. DB PostLike Table 의 memberId 에 Unique 제약조건을 걸어서 처리..?
 *
 * 현재는 memberId, postId 만 사용하지만,
 * memberDto, postDto 를 모두 받기 때문에 추후 다양한 기능을 추가할 수 있다.
 */
@Service
@RequiredArgsConstructor
public class PostLikeWriteService {

    private final PostLikeRepository postLikeRepository;

    /**
     * 단순히 PostLike 를 insert 하므로 update 처럼 락 처리를 하지 않아도 된다.
     */
    public Long create(PostDto postDto, MemberDto memberDto) {
        var postLike = PostLike.builder()
                .memberId(memberDto.id())
                .postId(postDto.id())
                .build();

        return postLikeRepository.save(postLike).getId();
    }
}
