package dev.practice.snsmysql.domain.post.repository.concurrent;

import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class LockTesterImpl implements LockTester {

    @Autowired
    PostRepository postRepository;

    @Transactional
    @Override
    public void findByIdUsingPessimisticWriteLockWithLockTimeout(Post post) {

        postRepository.findByIdUsingPessimisticWriteLock(post.getId());

        // 비관적 락 동작 하지 않음, 락 획득 성공
        Assertions.assertThat(true).isFalse();
    }

    @Transactional //Optimistic 은 Transactional 이 필요 없지만, JPA 를 위함(OSIV 대비)
    @Override
    public void likePostForOptimisticLockTest(Long postId) {

        Post findPost = postRepository.findById(postId).orElseThrow();

        findPost.increaseLikeCount();
    }
}
