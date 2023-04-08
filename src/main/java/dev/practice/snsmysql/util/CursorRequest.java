package dev.practice.snsmysql.util;

public record CursorRequest(
        Long key,
        int size
) {
    // Desc 정렬을 사용, key 는 PK로 사용할 것이므로.. AutoIncrement 에서 존재하지 않는 -1L 을 사용한다.
    // Desc 정렬시 마지막 key 값을 조회 하면 next key 로 -1L 을 준다.
    public static final Long NONE_KEY = -1L;

    public Boolean hasKey() {
        return key != null;
    }

    // 다음 조회 용, key 는 조회한 마지막 데이터의 key, 응답으로 내려준다.
    public CursorRequest next(Long key) {
        return new CursorRequest(key, size);
    }
}
