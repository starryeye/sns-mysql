package dev.practice.snsmysql.domain.follow.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 팔로우를 보여줄때 팔로우를 한 사람의 닉네임을 보여줘야 한다.
 * -> 팔로우 닉네임의 최신성은 보장되어야하므로.. 정규화의 대상이다.
 * --> 데이터의 조회 성능을 위해서 팔로우 닉네임을 따로 저장해두는 것은 닉네임 변경이 많이 일어나는 경우에는 비효율적이다.
 * --> 100만명의 팔로우를 가지고 있는 회원이 닉네임을 변경하면 100만명의 팔로우 닉네임을 변경해야 한다.
 * --> 쓰기 성능이 떨어진다.
 *
 * 그러면 정규화를 진행한다고 생각하고..
 * 닉네임을 Follow 에서 접근할때 방법은 3가지가 있다.
 * 1. Follow 에서 Member 를 조인한다.
 * 2. Follow 의 memberId 를 통해서 쿼리를 추가로 날려서 닉네임을 가져온다.
 * 3. Follow 에서 별도 저장소를 통해서 닉네임을 가져온다.
 * -> 1번은 프로젝트 초기에는 피하는게 좋다.. 아키텍처 설계 관점에서 강한 결합을 가지게 한다. 추후, 리팩토링이 어렵다.
 * -> 그래서 처음에는 결합을 낮추는데 집중한다. 성능상에서도 조인은 나중에 점점더 부담이 될 가능성이 크다.
 * -> 2번으로 진행해보겠다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long fromMemberId;
    @Column(nullable = false)
    private Long toMemberId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Follow(Long id, Long fromMemberId, Long toMemberId, LocalDateTime createdAt) {
        this.id = id;
        this.fromMemberId = Objects.requireNonNull(fromMemberId);
        this.toMemberId = Objects.requireNonNull(toMemberId);
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow follow = (Follow) o;
        return Objects.equals(id, follow.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
