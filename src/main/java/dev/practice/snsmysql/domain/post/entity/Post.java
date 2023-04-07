package dev.practice.snsmysql.domain.post.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Post {

    private final Long id;
    private final Long memberId; //게시물 작성자
    private final String contents;
    private final LocalDate createdDate; //게시물 작성 날짜
    private final LocalDateTime createdAt; //Entity 생성 시간

    @Builder
    public Post(Long id, Long memberId, String content, LocalDate createdDate, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = Objects.requireNonNull(memberId);
        this.contents = Objects.requireNonNull(content);
        this.createdDate = createdDate == null ? LocalDate.now() : createdDate;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }
}
