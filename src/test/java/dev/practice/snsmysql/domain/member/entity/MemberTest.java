package dev.practice.snsmysql.domain.member.entity;

import dev.practice.snsmysql.util.MemberFixtureFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @DisplayName("회원은 닉네임을 변경할 수 있다.")
    @Test
    void testChangeNickname() {

        //given
        var member = MemberFixtureFactory.create();
        var expected = "new";

        //when
        member.changeNickname(expected);

        //then
        Assertions.assertThat(member.getNickname()).isEqualTo(expected);
    }

    @DisplayName("회원의 닉네임은 10자를 넘길 수 없다.")
    @Test
    void testNicknameMaxLength() {

        //given
        var member = MemberFixtureFactory.create();
        var overMaxLengthNickname = "12345678901";

        Assertions.assertThatThrownBy(() -> member.changeNickname(overMaxLengthNickname))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("닉네임은 10자를 넘길 수 없습니다.");
    }

}