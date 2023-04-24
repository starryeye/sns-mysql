package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.entity.Timeline;
import dev.practice.snsmysql.domain.post.repository.custom.TimelineRepositoryCustom;
import dev.practice.snsmysql.domain.post.repository.custom.TimelineRepositoryCustomWithJdbc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineRepository extends JpaRepository<Timeline, Long>, TimelineRepositoryCustom, TimelineRepositoryCustomWithJdbc {

    /**
     * 아래는 size 를 받을 수 없는 구조라 오류
     */
//    List<Timeline> findAllByMemberId(Long memberId, int size, Sort sort);

    /**
     * 아래와 같이 limit 에 파라미터를 받아 올 수 없다..
     * 원래 jpql 은 limit 을 처리할 때, setMaxResults(size); 로 처리한다..
     */
//    @Query(value = "select t from Timeline t where t.memberId = :memberId order by t.id limit :size desc")
//    List<Timeline> findAllByMemberId(@Param("memberId")Long memberId, @Param("size")int size);

    //그래서 custom 으로 해결하였다.

    //TODO: Slice 사용
}
