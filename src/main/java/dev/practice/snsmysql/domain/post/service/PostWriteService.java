package dev.practice.snsmysql.domain.post.service;

import dev.practice.snsmysql.domain.post.dto.PostCommand;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostWriteService {

    private final PostRepository postRepository;

    public Long create(PostCommand command) {
        var post = Post.builder()
                .memberId(command.memberId())
                .content(command.contents())
                .build();

        return postRepository.save(post).getId();
    }

    public void likePost(Long postId) {
        /**
         * 현재 해당 로직은 조회 후, 좋아요 수를 증가시키고 저장하는 로직이다.
         * 트랜잭션 처리가 되어있지 않다.
         * 따라서, 동시에 여러명이 좋아요를 누르면, 좋아요 수가 올바르게 반영되지 않는다.
         */
        var post = postRepository.findById(postId).orElseThrow();
        post.increaseLikeCount(); //여기에 suspend(thread) 중단점 걸고 두번 요청하면 테스트 할 수 있다.
        //두번 요청 보낼 땐, swagger 에서는 지원이 되지 않기 때문에 postman 이나 터미널을 사용하자.
        postRepository.save(post);
    }
}
