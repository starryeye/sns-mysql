package dev.practice.snsmysql.domain.post.service;

import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.dto.PostDto;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostReadService {

    private final PostRepository postRepository;

    /**
     * 특정 회원의 특정 기간 동안의 게시물 수를 조회한다.
     */
    public List<DailyPostCount> getDailyPostCounts(DailyPostCountRequest request) {
        /**
         * 반환 값 : [작성 회원, 작성 날짜, 작성 게시물 수]
         *
         * sql
         * select memberId, createdDate, count(id)
         * from post
         * where memberId = :memberId and createdDate between firstDate and lastDate
         * group by createdDate memberId
         */

        return postRepository.groupByCreatedDate(request);
    }

    //TODO: Spring Data JPA 로 변경, PageRequest -> Pageable
    public Page<PostDto> getPosts(Long memberId, Pageable request) {

        var posts = postRepository.findAllByMemberId(memberId, request);

        return posts.map(this::toDto);
    }

    private PostDto toDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getMemberId(),
                post.getContents(),
                post.getCreatedDate());
    }
}
