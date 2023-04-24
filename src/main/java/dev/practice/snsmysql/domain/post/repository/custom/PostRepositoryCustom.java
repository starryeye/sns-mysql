package dev.practice.snsmysql.domain.post.repository.custom;

import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {

    /**
     * SQL :
     * SELECT member_id, created_date, count(id) as count
     * FROM %s
     * WHERE member_id = :memberId and created_date between :firstDate and :lastDate
     * GROUP BY member_id, created_date
     *
     * 동일한 기능을 하는 메서드가 PostRepository 에 존재한다.
     * -> countByMemberIdAndCreatedDateBetween
     */
    List<DailyPostCount> countByMemberIdAndCreatedDateByCustomJpql (DailyPostCountRequest request);

    /**
     * limit 을 변수로 받기 위함..
     */
    List<Post> findAllByMemberIdOrderByIdDescWithLimit(Long memberId, int size);
    List<Post> findAllByMemberIdAndIdLessThanOrderByIdDescWithLimit(Long id, Long memberId, int size);
}
