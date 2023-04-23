package dev.practice.snsmysql.domain.post.repository.concurrent;

import dev.practice.snsmysql.domain.post.entity.Post;

public interface LockTester {
    void findByIdUsingPessimisticWriteLockWithLockTimeout(Post post);
}
