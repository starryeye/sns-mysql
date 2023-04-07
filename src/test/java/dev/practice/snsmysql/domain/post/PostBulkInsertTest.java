package dev.practice.snsmysql.domain.post;

import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.PostRepository;
import dev.practice.snsmysql.util.PostFixtureFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.stream.IntStream;

@SpringBootTest
public class PostBulkInsertTest {

    @Autowired
    private PostRepository postRepository;

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
