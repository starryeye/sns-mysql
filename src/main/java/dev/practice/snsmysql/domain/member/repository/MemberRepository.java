package dev.practice.snsmysql.domain.member.repository;

import dev.practice.snsmysql.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByIdIn(List<Long> ids);
}
