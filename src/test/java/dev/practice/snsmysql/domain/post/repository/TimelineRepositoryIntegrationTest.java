package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.entity.Timeline;
import dev.practice.snsmysql.domain.post.repository.jdbc.TimelineRepositoryByJdbc;
import dev.practice.snsmysql.util.TimelineFixtureFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
class TimelineRepositoryIntegrationTest {
    @Autowired
    TimelineRepository timelineRepository;
    @Autowired
    TimelineRepositoryByJdbc timelineRepositoryByJdbc;
    @Autowired
    EntityManager entityManager;

    @BeforeEach
    public void setUp() {

        LocalDate end = LocalDate.now();
        LocalDate start = LocalDate.now().minusDays(5L);

        EasyRandom easyRandom1 = TimelineFixtureFactory.get(1L, start, end);
        EasyRandom easyRandom2 = TimelineFixtureFactory.get(2L, start, end);

        for(int i = 0; i < 10; i++) {
            Timeline timeline1 = easyRandom1.nextObject(Timeline.class);
            entityManager.persist(timeline1);
            Timeline timeline2 = easyRandom2.nextObject(Timeline.class);
            entityManager.persist(timeline2);
        }
    }

    @Test
    void integrationTest() {

        //given
        int size = 5;


        //when
        var timelineList1 = timelineRepository.findAllByMemberIdWithLimit(1L, size);
        var timelineList2 = timelineRepositoryByJdbc.findAllByMemberIdAndOrderByIdDesc(1L, size);

        var id = timelineList1.get(3).getId();
        var timelineList3 = timelineRepository.findAllByIdLessThanAndMemberIdWithLimit(id, 1L, size);
        var timelineList4 = timelineRepositoryByJdbc.findAllByLessThanIdAndMemberIdAndOrderByIdDesc(id, 1L, size);

        //then
        Assertions.assertThat(timelineList1).isNotNull();
        Assertions.assertThat(timelineList2).isNotNull();
        Assertions.assertThat(timelineList3).isNotNull();
        Assertions.assertThat(timelineList4).isNotNull();

        Assertions.assertThat(timelineList1.size()).isEqualTo(timelineList2.size());
        Assertions.assertThat(timelineList3.size()).isEqualTo(timelineList4.size());

        Assertions.assertThat(timelineList1).isEqualTo(timelineList2);
        Assertions.assertThat(timelineList3).isEqualTo(timelineList4);
    }

    @Test
    void bulkInsert() {
        //TODO
    }
}