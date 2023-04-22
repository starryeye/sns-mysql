package dev.practice.snsmysql.domain.post;

import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.jdbc.PostRepositoryByJdbc;
import dev.practice.snsmysql.util.PostFixtureFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.stream.IntStream;

@Execution(ExecutionMode.SAME_THREAD) //테스트가 여러개라도 하나씩 수행
@SpringBootTest
public class PostBulkInsertTest {

    //Bulk insert 는 jpa 에서 @GeneratedValue(strategy = GenerationType.IDENTITY) 를 사용하는 경우..
    //한방 쿼리가 불가능 하여 jdbcTemplate 와 같이 native query 를 사용해야한다..
    //TODO : JPA 의 @Query(native = true) 로 해결 검토
    @Autowired
    private PostRepositoryByJdbc postRepository;

    //벌크 데이터를 DB에 저장한다.
    @Test
    void bulkInsert() {
        var easyRandom = PostFixtureFactory.get(
                3L,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 2, 1)
        );

        var stopWatch = new StopWatch();
        stopWatch.start();

        //100만개 데이터 생성
        var posts = IntStream.range(0, 10000 * 100)
                .parallel() //병렬 처리, 객체 생성 시간이 오래 걸리기 때문에
                .mapToObj(i -> easyRandom.nextObject(Post.class))
                .toList();

        stopWatch.stop();
        System.out.println("객체 생성 시간 : " + stopWatch.getTotalTimeSeconds());

        var queryStopWatch = new StopWatch();
        queryStopWatch.start();

        //100만개 데이터 insert
        postRepository.bulkInsert(posts);

        queryStopWatch.stop();
        System.out.println("쿼리 실행 시간 : " + queryStopWatch.getTotalTimeSeconds());
    }
}
