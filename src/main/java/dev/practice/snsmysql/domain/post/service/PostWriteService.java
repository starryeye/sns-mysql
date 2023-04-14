package dev.practice.snsmysql.domain.post.service;

import dev.practice.snsmysql.domain.post.dto.PostCommand;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * select for update, 잠금 획득을 위해 @Transactional 을 사용한다.
     */
    @Transactional
    public void likePost(Long postId) {

        var post = postRepository.findById(postId, true).orElseThrow();
        post.increaseLikeCount(); //여기에 suspend(thread) 중단점 걸고 두번 요청하면 동시성 테스트 할 수 있다.
        //두번 요청 보낼 땐, swagger 에서는 탭을 두개를 띄어놓고하거나 postman 이나 터미널을 사용하자.
        postRepository.save(post);
    }

    /**
     * Optimistic Locking 을 사용하여 동시성 문제를 해결한다.
     * 낙관적 락 방법을 사용하기 때문에 select for update 를 사용하지 않는다.
     * 따라서, @Transactional 을 사용하지 않는다.
     */
    public void likePostByOptimisticLock(Long postId) {

        var post = postRepository.findById(postId, false).orElseThrow();
        post.increaseLikeCount(); //여기에 suspend(thread) 중단점 걸고 두번 요청하면 동시성 테스트 할 수 있다.
        //두번 요청 보낼 땐, swagger 에서는 탭을 두개를 띄어놓고하거나 postman 이나 터미널을 사용하자.
        postRepository.save(post);
    }
}
