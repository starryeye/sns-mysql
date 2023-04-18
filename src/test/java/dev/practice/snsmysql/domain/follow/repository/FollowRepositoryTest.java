package dev.practice.snsmysql.domain.follow.repository;

import dev.practice.snsmysql.domain.follow.entity.Follow;
import dev.practice.snsmysql.domain.follow.repository.jdbc.FollowRepositoryByJdbc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
class FollowRepositoryTest {

    @Autowired
    FollowRepository followRepository;
    @Autowired
    FollowRepositoryByJdbc followRepositoryByJdbc;

    private Follow follow1;
    private Follow follow2;

    @BeforeEach
    public void setUp() {
        follow1 = Follow.builder()
                .fromMemberId(1L)
                .toMemberId(2L)
                .createdAt(LocalDateTime.now())
                .build();
        follow2 = Follow.builder()
                .fromMemberId(1L)
                .toMemberId(3L)
                .createdAt(LocalDateTime.now())
                .build();

        follow1 = followRepository.save(follow1);
        follow2 = followRepository.save(follow2);
    }

    @Test
    void findAllByFromMemberId_JPA() {

        List<Follow> findFollows = followRepository.findAllByFromMemberId(1L);

        Assertions.assertThat(findFollows).hasSize(2);
        Assertions.assertThat(findFollows).containsExactlyInAnyOrder(follow1, follow2);
    }

    @Test
    void findAllByFromMemberId_JDBC() {

        List<Follow> findFollows = followRepositoryByJdbc.findAllByFromMemberId(1L);

        Assertions.assertThat(findFollows).hasSize(2);
        Assertions.assertThat(findFollows).containsExactlyInAnyOrder(follow1, follow2);
    }

    @Test
    void findAllByToMemberId_JPA() {

        List<Follow> findFollows = followRepository.findAllByToMemberId(2L);

        Assertions.assertThat(findFollows).hasSize(1);
        Assertions.assertThat(findFollows).contains(follow1);
    }

    @Test
    void findAllByToMemberId_JDBC() {

        List<Follow> findFollows = followRepositoryByJdbc.findAllByToMemberId(2L);

        Assertions.assertThat(findFollows).hasSize(1);
        Assertions.assertThat(findFollows).contains(follow1);
    }
}