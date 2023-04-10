package dev.practice.snsmysql.domain.post.dto;

import java.time.LocalDateTime;

public record TimelineDto(
        Long id,
        Long memberId,
        Long postId,
        LocalDateTime createdAt
) {
}
