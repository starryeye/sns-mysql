package dev.practice.snsmysql.domain.member.repository;

import dev.practice.snsmysql.domain.member.entity.MemberNicknameHistory;
import dev.practice.snsmysql.domain.member.repository.jdbc.MemberNicknameHistoryRepositoryByJdbc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
class MemberNicknameHistoryRepositoryIntegrationTest {

    @Autowired
    MemberNicknameHistoryRepositoryByJdbc memberNicknameHistoryRepositoryByJdbc;
    @Autowired
    MemberNicknameHistoryRepository memberNicknameHistoryRepository;

    private MemberNicknameHistory memberNicknameHistory1;
    private MemberNicknameHistory memberNicknameHistory2;
    private MemberNicknameHistory memberNicknameHistory3;

    @BeforeEach
    public void setUp() {
        memberNicknameHistory1 = MemberNicknameHistory.builder()
                .nickname("A")
                .memberId(1L)
                .createdAt(LocalDateTime.now())
                .build();
        memberNicknameHistory2 = MemberNicknameHistory.builder()
                .nickname("B")
                .memberId(2L)
                .createdAt(LocalDateTime.now())
                .build();
        memberNicknameHistory3 = MemberNicknameHistory.builder()
                .nickname("C")
                .memberId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        memberNicknameHistory1 = memberNicknameHistoryRepository.save(memberNicknameHistory1);
        memberNicknameHistory2 = memberNicknameHistoryRepository.save(memberNicknameHistory2);
        memberNicknameHistory3 = memberNicknameHistoryRepository.save(memberNicknameHistory3);
    }

    @Test
    void findAllByMemberId_JDBC() {

        List<MemberNicknameHistory> findNicknameHistories = memberNicknameHistoryRepositoryByJdbc.findAllByMemberId(1L);

        Assertions.assertThat(findNicknameHistories).hasSize(2);
        Assertions.assertThat(findNicknameHistories).contains(memberNicknameHistory1, memberNicknameHistory3);
        Assertions.assertThat(findNicknameHistories).doesNotContain(memberNicknameHistory2);
    }

    @Test
    void findAllByMemberId_JPA() {

        List<MemberNicknameHistory> findNicknameHistories = memberNicknameHistoryRepository.findAllByMemberId(1L);

        Assertions.assertThat(findNicknameHistories).hasSize(2);
        Assertions.assertThat(findNicknameHistories).contains(memberNicknameHistory1, memberNicknameHistory3);
        Assertions.assertThat(findNicknameHistories).doesNotContain(memberNicknameHistory2);
    }
}