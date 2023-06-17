package dev.practice.snsmysql.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String nickname;
    @Column(nullable = false, length = 20)
    private String email;
    @Column(nullable = false)
    private LocalDate birthday;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private static final Long NAME_MAX_LENGTH = 10L;

    @Builder
    public Member(Long id, String nickname, String email, LocalDate birthday, LocalDateTime createdAt) {
        this.id = id;

        validateNickname(nickname);
        this.nickname = Objects.requireNonNull(nickname);

        this.email = Objects.requireNonNull(email);
        this.birthday = Objects.requireNonNull(birthday);
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    public void changeNickname(String to) {
        Objects.requireNonNull(to);
        validateNickname(to);
        this.nickname = to;
    }

    void validateNickname(String nickname) {
        Assert.isTrue(nickname.length() <= NAME_MAX_LENGTH, "닉네임은 10자를 넘길 수 없습니다.");
    }

    /**
     * Object 객체의 equals (동등성 비교, 내부 값들이 같은가..) 는 == (동일성, 인스턴스가 완전히 같은가) 비교로 구현되어있다.
     *
     * 동일한 영속성 컨텍스트 내(동일한 트랜잭션) 에서 영속상태의 엔티티는 동일성이 보장된다.
     * -> 내부 1차 캐시에서 기본키와 객체를 map 으로 관리하고 동일한 기본키라면 동일한 인스턴스를 그대로 리턴
     * -> 따라서, 멀티 쓰레드에서 엔티티를 복잡하게 다뤄야할게 아니라면 일반적으로는 재정의할 필요가 없는 것 같다.
     *
     * 복합키, 값타입을 다룰때는 equals, hashcode 를 재정의하는 편이 일반적이다.
     *
     * equals 를 재정의하면 hashcode 도 재정의하는게 좋다. 그렇지 않으면 해시관련 collection 이 정상작동하지 않음.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
