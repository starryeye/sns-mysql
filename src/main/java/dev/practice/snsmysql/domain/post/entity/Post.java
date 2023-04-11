package dev.practice.snsmysql.domain.post.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Post {

    /**
     * 운영 중 이던 Post 테이블에 likeCount 컬럼이 추가되었다.
     * -> 토이프로젝트라면.. ddl default 값으로 처리하고 넘어가면 되지만..
     * -> 운영 중이라면.. 단순 default 값 처리 시, 테이블 락이 걸려서 성능 저하가 발생할 수 있다.
     * -> 그래서 별도의 마이그레이션 배치를 만들어서 조금씩 채워 넣거나, 조회시점에 null 이면 값을 채워 넣는 방식을 채택한다.
     */

    private final Long id;
    private final Long memberId; //게시물 작성자
    private final String contents;
    private final LocalDate createdDate; //게시물 작성 날짜
    private final Long likeCount; //좋아요 수

    private final LocalDateTime createdAt; //Entity 생성 시간

    @Builder
    public Post(Long id, Long memberId, String content, LocalDate createdDate, Long likeCount, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = Objects.requireNonNull(memberId);
        this.contents = Objects.requireNonNull(content);
        this.createdDate = createdDate == null ? LocalDate.now() : createdDate;
        this.likeCount = likeCount == null ? 0L : likeCount; //조회 시점에 null 이면 0으로 채워 진다.
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }
}
