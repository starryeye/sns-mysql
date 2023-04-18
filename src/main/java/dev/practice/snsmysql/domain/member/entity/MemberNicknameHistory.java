package dev.practice.snsmysql.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 데이터의 최신성을 보장하는 데이터가 아니면 정규화의 대상이 아니다.
 * -> 과거의 데이터를 가지고 있어야 하는 History 성 데이터면 정규화의 대상이 아니다.
 *
 * 실무에서 모호한 것..
 * -> 주문 데이터에서 제조사의 이름을 가지고 있어야 한다면.. 제조사 이름의 최신성을 유지해야하는가?
 * --> 요구사항을 PO, PM, RM, 기획자와 논의해야 한다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberNicknameHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;
    @Column(nullable = false, length = 20)
    private String nickname;
    @Column(nullable = false)
    private LocalDateTime createdAt; //TODO: BaseEntity 를 상속받아서 처리하는 것이 좋다.

    @Builder
    public MemberNicknameHistory(Long id, Long memberId, String nickname, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = Objects.requireNonNull(memberId);
        this.nickname = Objects.requireNonNull(nickname);
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberNicknameHistory memberNicknameHistory = (MemberNicknameHistory) o;
        return Objects.equals(id, memberNicknameHistory.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
