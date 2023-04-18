package dev.practice.snsmysql.domain.member.repository;

import dev.practice.snsmysql.domain.member.entity.MemberNicknameHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberNicknameHistoryRepository extends JpaRepository<MemberNicknameHistory, Long> {

    //SQL : SELECT * FROM %s WHERE memberId = :memberId
    //JPQL : select m from MemberNicknameHistory m where m.memberId = :memberId
    List<MemberNicknameHistory> findAllByMemberId(Long memberId);
}
