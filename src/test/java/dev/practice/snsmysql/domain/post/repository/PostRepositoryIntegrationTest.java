package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.concurrent.LockTester;
import dev.practice.snsmysql.domain.post.repository.jdbc.PostRepositoryByJdbc;
import dev.practice.snsmysql.util.PostFixtureFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@SpringBootTest
class PostRepositoryIntegrationTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    PostRepositoryByJdbc postRepositoryByJdbc;
    @Autowired
    EntityManager entityManager;

    @Autowired
    LockTester lockTester;

    private Post postOne;

    @BeforeEach
    public void setUp() {
        LocalDate end = LocalDate.now();
        LocalDate start = LocalDate.now().minusDays(5L);

        EasyRandom easyRandom1 = PostFixtureFactory.get(1L, start, end);
        EasyRandom easyRandom2 = PostFixtureFactory.get(2L, start, end);

        for (int i = 0; i < 10; i++) {
            Post post1 = easyRandom1.nextObject(Post.class);
            Post post2 = easyRandom2.nextObject(Post.class);

            postRepository.saveAll(Arrays.asList(post1, post2));
        }
    }

    @BeforeEach
    public void setUpOne() {
        LocalDate now = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now();
        Long memberId = 1L;

        Post post = Post.builder()
                .memberId(1L)
                .content("content")
                .createdDate(now)
                .likeCount(0L)
                .version(0L)
                .createdAt(nowTime)
                .build();

        postOne = postRepository.save(post);
    }

    @Test
    @Transactional
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

    @Test
    void findAllByMemberId() {
        //given
        int page = 2;
        int size = 3;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");

        Pageable request = PageRequest.of(page, size, sort);

        //when
        Page<Post> jpa = postRepository.findAllByMemberId(2L, request);
        Page<Post> jdbcTemplate = postRepositoryByJdbc.findAllByMemberId(2L, request);

        //then
        Assertions.assertThat(jpa).isNotNull();
        Assertions.assertThat(jdbcTemplate).isNotNull();

        Assertions.assertThat(jpa.getSize()).isEqualTo(jdbcTemplate.getSize());

        Assertions.assertThat(jpa).isEqualTo(jdbcTemplate);
    }

    /**
     * 비관적 락 테스트 with JPA
     */
    @Test
    @Transactional
    void findByIdWithPessimisticReadLock_JPA() throws Throwable {

        //when
        Post lockedPost = postRepository.findByIdUsingPessimisticWriteLock(postOne.getId()).orElseThrow();

        lockedPost.increaseLikeCount();

        Assertions.assertThat(lockedPost.getLikeCount()).isEqualTo(1);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //then
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Object> future = executorService.submit(() -> {
            /**
             * 해당 로직은 새로운 스레드에서 동작되는데..
             * 다른 스레드에서 호출하더라도..
             * 아래 메서드를 내부 클래스로 호출하면 @Transactional 이 적용되지 않는다.
             *
             * 참고>
             * 기본적으로 서로 다른 스레드는 트랜잭션 전파가 이루어지지 않는다.
             */
            lockTester.findByIdUsingPessimisticWriteLockWithLockTimeout(postOne);

            return null; //의미 없음, 무조건 예외 발생함
        });

        //스레드들(submit 하나 하나, 현재 예제에서는 1개)의 작업이 모두 완료되면 스레드 풀을 종료하도록 요청한다.
        executorService.shutdown();

        try {
            //executorService.submit() 의 결과를 가져온다.
            //60초 이내에 결과가 없으면 TimeoutException 발생 or 작업 완료 될때 까지 대기
            //timeout 설정이 없고 작업 완료 되지 않으면 무한 대기 이다.
            future.get(60, TimeUnit.SECONDS);

        } catch (ExecutionException e) {

            Throwable cause = e.getCause();

            if (cause instanceof PessimisticLockingFailureException) {
                log.info("PessimisticLockingFailureException 발생, success");
                Assertions.assertThat(true).isTrue();
            } else {
                throw cause;
            }
        } catch (TimeoutException e) {
            log.info("TimeoutException 발생, fail"); //future.get() 의 timeout 처리
            Assertions.assertThat(false).isTrue();
        }

        //아래는 비관적 락 대기 시간 측정을 위함

        //스레드풀이 종료될때까지 65 초간 기다린다.
        //shutdown() 이 없으면 스레드가 종료되어도 계속 기다림, 스레드 대기가 아니라 스레드 풀 대기이기 때문
        if (executorService.awaitTermination(65, TimeUnit.SECONDS)) {

            stopWatch.stop();
            log.info("jop is terminate, Waiting time : {}", stopWatch.getTotalTimeSeconds());
            //50초간 기다리고 정상 종료 되는 것으로 봐서 innodb lock wait timeout 이 50초로 설정되어 있는듯
        } else {
            //아래는 innodb lock wait time 이 65초 이상 이거나
            //다른 이유로 스레드가 종료되지 않은 경우 (65초 대기)
            stopWatch.stop();
            log.info("jop is not terminate, Waiting time : {}", stopWatch.getTotalTimeSeconds());
            Assertions.assertThat(true).isFalse();
        }
    }

    @Test
    @Transactional
    void findByIdWithPessimisticReadLock_Jdbc() {
        //TODO
    }

    @Test
    @Transactional
    void findAllByIdIn() {
        //given
        Long memberId = 2L;
        int page = 0;
        int size = 3;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");

        Pageable pageable = PageRequest.of(page, size, sort);

        List<Long> postIdList = postRepository.findAllByMemberId(memberId, pageable)
                .getContent().stream()
                .map(Post::getId)
                .toList();

        entityManager.flush();
        entityManager.clear();

        //when
        List<Post> jpa = postRepository.findAllByIdIn(postIdList);
        List<Post> jdbcTemplate = postRepositoryByJdbc.findAllByInIds(postIdList);

        //then
        Assertions.assertThat(jpa).isNotNull();
        Assertions.assertThat(jdbcTemplate).isNotNull();

        Assertions.assertThat(jpa.size()).isEqualTo(jdbcTemplate.size());

        Assertions.assertThat(jpa).isEqualTo(jdbcTemplate);
    }

    /**
     * Cursor 기반 Pagination
     * 최초 페이지
     */
    @Test
    void findAllByMemberIdOrderByIdDescWithLimit() {

        //given
        Long memberId = 2L;
        int size = 5;

        //when
        List<Post> jpa = postRepository.findAllByMemberIdOrderByIdDescWithLimit(memberId, size);
        List<Post> jdbcTemplate = postRepositoryByJdbc.findAllByMemberIdAndOrderByIdDesc(memberId, size);

        //then
        Assertions.assertThat(jpa).isNotNull();
        Assertions.assertThat(jdbcTemplate).isNotNull();

        Assertions.assertThat(jpa.size()).isEqualTo(jdbcTemplate.size());

        Assertions.assertThat(jpa).isEqualTo(jdbcTemplate);
    }

    /**
     * Cursor 기반 Pagination
     * 두번째 페이지부터의 요청 처리
     */
    @Test
    void findAllByMemberIdAndIdLessThanOrderByIdDescWithLimit() {

        //given
        Long memberId = 1L;
        int size = 4;

        List<Post> postList = postRepository.findAllByMemberIdOrderByIdDescWithLimit(memberId, size);
        entityManager.clear();

        Long lastId = postList.stream()
                .mapToLong(Post::getId)
                .min()
                .orElse(-1L);
        log.info("last id = {}", lastId);

        //when
        List<Post> jpa = postRepository.findAllByMemberIdAndIdLessThanOrderByIdDescWithLimit(lastId, memberId, size);
        List<Post> jdbcTemplate = postRepositoryByJdbc.findAllByLessThanIdAndMemberIdAndOrderByIdDesc(lastId, memberId, size);

        //then
        Assertions.assertThat(jpa).isNotNull();
        Assertions.assertThat(jdbcTemplate).isNotNull();

        Assertions.assertThat(jpa.size()).isEqualTo(jdbcTemplate.size());

        Assertions.assertThat(jpa).isEqualTo(jdbcTemplate);
    }
}