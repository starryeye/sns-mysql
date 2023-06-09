package dev.practice.snsmysql.domain.follow.repository.jdbc;

import dev.practice.snsmysql.domain.follow.entity.Follow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryByJdbc {

    private static final String TABLE_NAME = "follow";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Follow> rowMapper = (resultSet, rowNum) -> Follow.builder()
            .id(resultSet.getLong("id"))
            .fromMemberId(resultSet.getLong("from_member_id"))
            .toMemberId(resultSet.getLong("to_member_id"))
            .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
            .build();

    public List<Follow> findAllByFromMemberId(Long fromMemberId) {

        var sql = String.format("SELECT * FROM %s WHERE from_member_id = :fromMemberId", TABLE_NAME);

        var param = new MapSqlParameterSource()
                .addValue("fromMemberId", fromMemberId);

        return namedParameterJdbcTemplate.query(sql, param, rowMapper);
    }

    public List<Follow> findAllByToMemberId(Long toMemberId) {

        var sql = String.format("SELECT * FROM %s WHERE to_member_id = :toMemberId", TABLE_NAME);

        var param = new MapSqlParameterSource()
                .addValue("toMemberId", toMemberId);

        return namedParameterJdbcTemplate.query(sql, param, rowMapper);
    }

    public Follow save(Follow follow) {

        if(follow.getId() == null) {
            return insert(follow);
        }

        throw new UnsupportedOperationException("Follow 는 update 를 지원하지 않습니다.");
    }

    private Follow insert(Follow follow) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");

        SqlParameterSource param = new BeanPropertySqlParameterSource(follow);
        var id = jdbcInsert.executeAndReturnKey(param).longValue();

        return Follow.builder()
                .id(id)
                .fromMemberId(follow.getFromMemberId())
                .toMemberId(follow.getToMemberId())
                .createdAt(follow.getCreatedAt())
                .build();
    }
}
