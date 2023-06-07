package dev.practice.snsmysql.application.controller;

import dev.practice.snsmysql.application.usecase.CreatePostLikeUsecase;
import dev.practice.snsmysql.application.usecase.CreatePostUsecase;
import dev.practice.snsmysql.application.usecase.GetTimelinePostsUsecase;
import dev.practice.snsmysql.domain.post.dto.DailyPostCount;
import dev.practice.snsmysql.domain.post.dto.DailyPostCountRequest;
import dev.practice.snsmysql.domain.post.dto.PostCommand;
import dev.practice.snsmysql.domain.post.dto.PostDto;
import dev.practice.snsmysql.domain.post.service.PostReadService;
import dev.practice.snsmysql.domain.post.service.PostWriteService;
import dev.practice.snsmysql.util.CursorRequest;
import dev.practice.snsmysql.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostWriteService postWriteService;
    private final GetTimelinePostsUsecase getTimelinePostsUsecase;
    private final PostReadService postReadService;

    //fan out on write, 게시물 등록 및 타임라인 등록
    private final CreatePostUsecase createPostUsecase;

    //좋아요 기능, PostLike Table 에 데이터를 등록한다.
    private final CreatePostLikeUsecase createPostLikeUsecase;

    @PostMapping
    public Long create(PostCommand postCommand) {
        //return postWriteService.create(postCommand); //fan out on read
        return createPostUsecase.execute(postCommand); //fan out on write
    }

    @GetMapping("/daily-post-counts")
    public List<DailyPostCount> getDailyPostCounts(@ModelAttribute DailyPostCountRequest request) {
        return postReadService.getDailyPostCounts(request);
    }

    /**
     * offset 기반 pagination
     *
     * 특정 회원의 게시물 목록을 조회한다.
     * pageable 에 데이터를 넣을 때, "createdDate,desc" 형식으로 넣어야 한다.
     */
    @GetMapping("/members/{memberId}")
    public Page<PostDto> getPosts(
            @PathVariable Long memberId,
            Pageable pageable
    ) {
        return postReadService.getPosts(memberId, pageable);
    }

    /**
     * cursor 기반 pagination
     */
    @GetMapping("/members/{memberId}/by-cursor")
    public PageCursor<PostDto> getPostsByCursor(
            @PathVariable Long memberId,
            @ModelAttribute CursorRequest cursorRequest
    ) {
        return postReadService.getPosts(memberId, cursorRequest);
    }

    /**
     * timeline 기능, cursor 기반 pagination
     */
    @GetMapping("/members/{memberId}/timeline")
    public PageCursor<PostDto> getTimelinePosts(
            @PathVariable Long memberId,
            @ModelAttribute CursorRequest cursorRequest
    ) {
//        return getTimelinePostsUsecase.execute(memberId, cursorRequest); //fan out on read
        return getTimelinePostsUsecase.executeByTimeline(memberId, cursorRequest); //fan out on write
    }

    /**
     * 게시글 좋아요 기능
     */
    @PostMapping("/{postId}/like/v1")
    public void likePost(@PathVariable Long postId) {
//        postWriteService.likePost(postId); //비관적 락
        postWriteService.likePostByOptimisticLock(postId); //낙관적 락
    }

    /**
     * 게시글 좋아요 기능, PostLike 테이블에 데이터를 저장하는 방식
     */
    @PostMapping("/{postId}/like/v2")
    public void likePost(@PathVariable Long postId, @RequestParam Long memberId) {
        createPostLikeUsecase.execute(postId, memberId);
    }

    /**
     * TODO: 현재 게시물 조회에서 좋아요 수는 likeCount 필드를 바라봐서..
     * 좋아요 방식을 v2로 동작시키면 조회 때 좋아요 수가 제대로 나오지 않는다.
     * v2 의 좋아요 수는 PostLike 테이블에서 조회하는 것이 맞다.
     * 그러나, 조회 때마다 PostLike 테이블에 Count 쿼리를 날리는 것은 조회 성능에 굉장히 안좋다.
     *
     * 1. 그래서, 주기적 비동기로 PostLike 테이블에 Count 쿼리를 날려서..
     * Post 테이블의 likeCount 필드를 업데이트 하는 방식을 사용하면 좋을 것 같다.
     * or
     * 2. Redis Data Type 을 적절히 활용하여 게시물별 좋아요수를 업데이트하고 조회하도록 해보자..
     * -> PostLike 테이블은 히스토리를 위해서  유지하고
     * -> 좋아요 수 집계를 위한 부분만 Redis 로 해보도록 한다.
     * -> 좋아요 수 조회와 좋아요 수 업데이트에 대해 O(1) 로 보장되는.. Redis Hashes 로 구현해보자.
     * -> Redis 의 특징으로 race condition 을 걱정할 필요가 없다.
     * -> Key: post:{postId}, field: likeCount, value: {좋아요 수}
     *
     * TODO: Key: post:postLike, field: postId, value: 객체..
     * -> TODO: 게시글이 40억개가 넘어간다면? (Redis Hashes 최대 field-value) 고민해볼것..
     * -> -> Cluster(shard)
     * -> -> 1번 방법이 나을듯..
     */
}
