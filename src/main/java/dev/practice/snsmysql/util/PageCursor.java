package dev.practice.snsmysql.util;

import java.util.List;

/**
 * cursor 기반 페이징 방식에서 사용할 응답 데이터
 * Page 객체 대용
 */
public record PageCursor<T>(
        CursorRequest cursorRequest,
        List<T> contents
) {
}
