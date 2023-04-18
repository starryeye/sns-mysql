package dev.practice.snsmysql.domain.member.repository;

import dev.practice.snsmysql.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    //SQL : SELECT * FROM %s WHERE id IN (:ids)
    //JPQL : select m from Member m where id in (:ids)
    List<Member> findAllByIdIn(List<Long> ids);
}
