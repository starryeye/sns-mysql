package dev.practice.snsmysql.domain.post.repository;

import dev.practice.snsmysql.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private static final String TABLE_NAME = "post";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Post save(Post post) {
        if (post.getId() == null) {
            return insert(post);
        }

        throw new UnsupportedOperationException("Post 는 update 를 지원하지 않습니다.");
    }

    private Post insert(Post post) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");

        SqlParameterSource param = new BeanPropertySqlParameterSource(post);

        var id = insert.executeAndReturnKey(param).longValue();

        return Post.builder()
                .id(id)
                .memberId(post.getMemberId())
                .content(post.getContents())
                .createdDate(post.getCreatedDate())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
