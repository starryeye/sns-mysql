package dev.practice.snsmysql.domain.post.service;

import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.dto.PostDto;
import dev.practice.snsmysql.domain.post.entity.Post;
import dev.practice.snsmysql.domain.post.repository.PostRepository;
import dev.practice.snsmysql.util.CursorRequest;
import dev.practice.snsmysql.util.PageCursor;
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

        //TODO: 리턴 값을 Page<PostDto> 로 변경 고려
        var posts = postRepository.findAllByMemberId(memberId, request);

        return posts.map(this::toDto);
    }

    public PageCursor<PostDto> getPosts(Long memberId, CursorRequest request) {

        var posts = findAllBy(memberId, request);

        //추출한 posts 에서 마지막 id 를 추출한다.
        var lastKey = getLastKey(posts);

        //다음 조회에서 사용할 CursorRequest 와 조회한 posts 를 PageCursor 로 반환한다.
        return new PageCursor<>(request.next(lastKey), posts.stream().map(this::toDto).toList());
    }

    public PageCursor<PostDto> getPosts(List<Long> memberIds, CursorRequest request) {

        var posts = findAllBy(memberIds, request);

        //추출한 posts 에서 마지막 id 를 추출한다.
        var lastKey = getLastKey(posts);

        //다음 조회에서 사용할 CursorRequest 와 조회한 posts 를 PageCursor 로 반환한다.
        return new PageCursor<>(request.next(lastKey), posts.stream().map(this::toDto).toList());
    }

    public List<PostDto> getPosts(List<Long> ids) {
        return postRepository.findAllByInIds(ids).stream()
                .map(this::toDto)
                .toList();
    }

    private List<Post> findAllBy(Long memberId, CursorRequest request) {

        if(request.hasKey()) {
            return postRepository.findAllByLessThanIdAndMemberIdAndOrderByIdDesc(request.key(), memberId, request.size());
        }

        return postRepository.findAllByMemberIdAndOrderByIdDesc(memberId, request.size());
    }

    private List<Post> findAllBy(List<Long> memberIds, CursorRequest request) {

        if(request.hasKey()) {
            return postRepository.findAllByLessThanIdAndInMemberIdsAndOrderByIdDesc(request.key(), memberIds, request.size());
        }

        return postRepository.findAllByInMemberIdsAndOrderByIdDesc(memberIds, request.size());
    }

    private static long getLastKey(List<Post> posts) {
        return posts.stream()
                .mapToLong(Post::getId)
                .min()
                .orElse(CursorRequest.NONE_KEY);
    }

    private PostDto toDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getMemberId(),
                post.getContents(),
                post.getCreatedDate());
    }
}
