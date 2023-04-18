package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.custom.PostRepositoryCustom;
import dev.practice.snsmysql.util.PageHelper;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

//    var sql = String.format(
//            """
//            SELECT memberId, createdDate, count(id) as count
//            FROM %s
//            WHERE memberId = :memberId and createdDate between :firstDate and :lastDate
//            GROUP BY memberId, createdDate
//            """, TABLE_NAME
//    ); //language=MySQL
    @Query("SELECT new dev.practice.snsmysql.domain.post.dto.DailyPostCount(p.memberId, p.createdDate, COUNT(p.id)) " +
            "FROM Post p " +
            "WHERE p.memberId = :memberId AND p.createdDate BETWEEN :firstDate AND :lastDate " +
            "GROUP BY p.memberId, p.createdDate")
    List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request);


//    var sql = String.format(
//            """
//            SELECT *
//            FROM %s
//            WHERE memberId = :memberId
//            ORDER BY %s
//            LIMIT :size
//            OFFSET :offset
//            """, TABLE_NAME, PageHelper.orderBy(pageable.getSort())
//    ); //language=MySQL
    Page<Post> findPagedAllByMemberId(Long memberId, Pageable pageable);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :postId")
    Optional<Post> findByIdWithPessimisticLock(Long postId);


//    var sql = String.format(
//            """
//            SELECT *
//            FROM %s
//            WHERE id in (:ids)
//            """, TABLE_NAME
//    );
    List<Post> findAllByIdIn(List<Long> ids);


//    var sql = String.format(
//            """
//            SELECT *
//            FROM %s
//            WHERE memberId = :memberId
//            ORDER BY id DESC
//            LIMIT :size
//            """, TABLE_NAME
//    );
    List<Post> findAllByMemberId(Long memberId, Pageable pageable);


//    var sql = String.format(
//            """
//            SELECT *
//            FROM %s
//            WHERE memberId in (:memberIds)
//            ORDER BY id DESC
//            LIMIT :size
//            """, TABLE_NAME
//    );
    List<Post> findAllByMemberIdIn(List<Long> memberIds, Pageable pageable);


//    var sql = String.format(
//            """
//            SELECT *
//            FROM %s
//            WHERE memberId = :memberId and id < :id
//            ORDER BY id DESC
//            LIMIT :size
//            """, TABLE_NAME
//    );
    List<Post> findAllByMemberIdAndIdLessThan(Long memberId, Long id, Pageable pageable);


//    var sql = String.format(
//            """
//            SELECT *
//            FROM %s
//            WHERE memberId in (:memberIds) and id < :id
//            ORDER BY id DESC
//            LIMIT :size
//            """, TABLE_NAME
//    );
    List<Post> findAllByMemberIdInAndIdLessThan(List<Long> memberIds, Long id, Pageable pageable);

    //update, optimistic lock
//    var sql = String.format(
//            """
//            UPDATE %s SET
//                memberId = :memberId,
//                contents = :contents,
//                createdDate = :createdDate,
//                likeCount = :likeCount,
//                createdAt = :createdAt,
//                version = :version + 1
//            WHERE id = :id and version = :version
//            """, TABLE_NAME
//    );
}
