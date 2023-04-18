package dev.practice.snsmysql.domain.follow.repository;

import dev.practice.snsmysql.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    //SQL : SELECT * FROM %s WHERE fromMemberId = :fromMemberId
    //JPQL : select f from follow f where f.fromMemberId = :fromMemberId
    List<Follow> findAllByFromMemberId(Long fromMemberId);

    //SQL : SELECT * FROM %s WHERE toMemberId = :toMemberId
    //JPQL : select f from follow f where f.toMemberId = :toMemberId
    List<Follow> findAllByToMemberId(Long toMemberId);
}
