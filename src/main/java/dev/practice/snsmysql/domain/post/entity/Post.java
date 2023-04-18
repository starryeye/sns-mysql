package dev.practice.snsmysql.domain.post.entity;

import dev.practice.snsmysql.domain.member.entity.MemberNicknameHistory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    /**
     * 운영 중 이던 Post 테이블에 likeCount 컬럼이 추가되었다.
     * -> 토이프로젝트라면.. ddl default 값으로 처리하고 넘어가면 되지만..
     * -> 운영 중이라면.. 단순 default 값 처리 시, 테이블 락이 걸려서 성능 저하가 발생할 수 있다.
     * -> 그래서 별도의 마이그레이션 배치를 만들어서 조금씩 채워 넣거나, 조회시점에 null 이면 값을 채워 넣는 방식을 채택한다.
     *
     * version 컬럼도 운영 중이던 상황에서 추가 되었다.
     * -> 해당 컬럼은 default 값을 ddl 로 처리해줘야 한다.
     * -> 운영 중이던 상황에서 추가 되었기 때문에, 기존 데이터에는 null 이 들어가 있을 수 있다.
     * -> 최초 조회 시점에 null 이면 0으로 채워 주는데 (application 에는 0으로 채워짐)
     * -> update 쿼리에 where 문 조건에 version 컬럼이 들어가서 DB 에는 null 이고 application 에는 0 이라서 update 가 안된다.
     * -> likeCount 에서는 그냥 application 값이 바로 DB 에 반영되기 때문에 문제가 없었지만, version 은 그렇지 않다.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId; //게시물 작성자
    @Column(nullable = false, length = 100)
    private String contents;
    @Column(nullable = false)
    private LocalDate createdDate; //게시물 작성 날짜
    @Column(nullable = false)
    private Long likeCount; //좋아요 수

    @Version
    private Long version; //낙관적 락, JPA Optimistic Locking

    @Column(nullable = false)
    private LocalDateTime createdAt; //Entity 생성 시간

    @Builder
    public Post(Long id, Long memberId, String content, LocalDate createdDate, Long likeCount, Long version, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = Objects.requireNonNull(memberId);
        this.contents = Objects.requireNonNull(content);
        this.createdDate = createdDate == null ? LocalDate.now() : createdDate;
        this.likeCount = likeCount == null ? 0L : likeCount; //조회 시점에 null 이면 0으로 채워 진다.
        this.version = version == null ? 0L : version;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    public void increaseLikeCount() {
        this.likeCount++;

        //version 컬럼은 repository 에서 처리한다. 좋아요 수 증가에만 적용하는게 아닌.. 다른 로직에서도 적용해야 하기 때문이다.
        //따라서 모든 update 로직에서 처리하기 위해 repository 에서 처리한다.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
