package dev.practice.snsmysql.domain.post.dto;

public record PostCommand(
        Long memberId,
        String contents
) {
}
