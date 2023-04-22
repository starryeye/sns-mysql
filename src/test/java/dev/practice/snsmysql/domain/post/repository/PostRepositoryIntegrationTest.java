package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.entity.Timeline;
import dev.practice.snsmysql.domain.post.repository.jdbc.PostRepositoryByJdbc;
import dev.practice.snsmysql.util.PostFixtureFactory;
import dev.practice.snsmysql.util.TimelineFixtureFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class PostRepositoryIntegrationTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    PostRepositoryByJdbc postRepositoryByJdbc;
    @Autowired
    EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        LocalDate end = LocalDate.now();
        LocalDate start = LocalDate.now().minusDays(5L);

        EasyRandom easyRandom1 = PostFixtureFactory.get(1L, start, end);
        EasyRandom easyRandom2 = PostFixtureFactory.get(2L, start, end);

        for(int i = 0; i < 10; i++) {
            Post post1 = easyRandom1.nextObject(Post.class);
            Post post2 = easyRandom2.nextObject(Post.class);

            postRepository.saveAll(Arrays.asList(post1, post2));
        }
    }

    @Test
    void countByMemberIdAndCreatedDate() {

        //given
        Long memberId = 1L;
        LocalDate firstDate = LocalDate.now().minusDays(2L);
        LocalDate lastDate = LocalDate.now();

        DailyPostCountRequest request = new DailyPostCountRequest(memberId, firstDate, lastDate);

        //when

        log.info("jpql start");
        List<DailyPostCount> jpa1 = postRepository.countByMemberIdAndCreatedDateByCustomJpql(request);

        entityManager.flush();
        entityManager.clear();

        log.info("jpa method query start");
        List<DailyPostCount> jpa2 = postRepository.countByMemberIdAndCreatedDateBetween(request);

        log.info("jdbcTemplate start");
        List<DailyPostCount> jdbcTemplate = postRepositoryByJdbc.groupByCreatedDate(request);

        //then
        Assertions.assertThat(jpa1).isNotNull();
        Assertions.assertThat(jpa2).isNotNull();
        Assertions.assertThat(jdbcTemplate).isNotNull();

        Assertions.assertThat(jpa1.size()).isEqualTo(jdbcTemplate.size());
        Assertions.assertThat(jpa1.size()).isEqualTo(jpa2.size());

        Assertions.assertThat(jpa1).isEqualTo(jdbcTemplate);
        Assertions.assertThat(jpa1).isEqualTo(jpa2);
    }

}