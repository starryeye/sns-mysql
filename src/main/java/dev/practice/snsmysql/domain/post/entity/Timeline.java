package dev.practice.snsmysql.domain.post.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Fan out on write 를 위한 테이블
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId; // 게시물을 작성한 회원을 팔로우한 회원 id
    private Long postId; // 게시물 id
    private LocalDateTime createdAt;

    @Builder
    public Timeline(Long id, Long memberId, Long postId, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = Objects.requireNonNull(memberId);
        this.postId = Objects.requireNonNull(postId);

        // TODO: 나머지 엔티티에도 적용, BaseEntity 에서 아래 로직 처리
        // id 가 null 이면(생성 시점), 생성자에서 현재 시간을 넣어줄 수 있고
        // id 가 null 이 아니면(수정 시점), 이미 한번 생성된 객체이므로 생성자에서 현재 시간을 넣어줄 수 없다.
        if(id == null)
            this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        else {
            if(createdAt == null)
                throw new IllegalArgumentException("createdAt is null");
            this.createdAt = createdAt;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timeline timeline = (Timeline) o;
        return Objects.equals(id, timeline.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
