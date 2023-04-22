package dev.practice.snsmysql.domain.member.repository;

import dev.practice.snsmysql.domain.member.entity.Member;
import dev.practice.snsmysql.domain.member.repository.jdbc.MemberRepositoryByJdbc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
class MemberRepositoryIntegrationTest {

    @Autowired
    MemberRepositoryByJdbc memberRepositoryByJdbc;
    @Autowired
    MemberRepository memberRepository;

    private Member member1;
    private Member member2;
    private Member member3;

    @BeforeEach
    public void setUp() {
        member1 = Member.builder()
                .nickname("A")
                .email("A@A.a")
                .birthday(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();
        member2 = Member.builder()
                .nickname("B")
                .email("B@B.b")
                .birthday(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();
        member3 = Member.builder()
                .nickname("C")
                .email("C@C.c")
                .birthday(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        member1 = memberRepository.save(member1);
        member2 = memberRepository.save(member2);
        member3 = memberRepository.save(member3);
    }

    @Test
    void findAllByIdIn_JDBC() {

        List<Long> ids = Arrays.asList(member1.getId(), member3.getId());

        List<Member> findMembers = memberRepositoryByJdbc.findAllByIdIn(ids);

        Assertions.assertThat(findMembers).hasSize(2);
        Assertions.assertThat(findMembers).contains(member1, member3);
        Assertions.assertThat(findMembers).doesNotContain(member2);
    }

    @Test
    void findAllByIdIn_JPA() {

        List<Long> ids = Arrays.asList(member1.getId(), member3.getId());

        List<Member> findMembers = memberRepository.findAllByIdIn(ids);

        Assertions.assertThat(findMembers).hasSize(2);
        Assertions.assertThat(findMembers).contains(member1, member3);
        Assertions.assertThat(findMembers).doesNotContain(member2);
    }
}