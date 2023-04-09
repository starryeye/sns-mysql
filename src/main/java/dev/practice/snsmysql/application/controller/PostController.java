package dev.practice.snsmysql.application.controller;

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
import org.springframework.data.domain.PageRequest;
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
}
