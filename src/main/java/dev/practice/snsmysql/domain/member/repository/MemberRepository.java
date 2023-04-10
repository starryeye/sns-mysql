package dev.practice.snsmysql.domain.member.repository;

import dev.practice.snsmysql.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * TODO : JPA 로 변경
 *
 * TODO: SqlParameterSource 의 3가지 구현체 공부 해볼 것
 */
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String TABLE_NAME = "Member";

    private static final RowMapper<Member> rowMapper = (ResultSet resultSet, int rowNum) -> Member.builder()
            .id(resultSet.getLong("id"))
            .email(resultSet.getString("email"))
            .nickname(resultSet.getString("nickname"))
            .birthday(resultSet.getObject("birthday", LocalDate.class))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class)) //columnLabel.. DB 에서 컬럼명이 create_at 이 아니고 createAt 이라서..
            .build();

    public Optional<Member> findById(Long id) {
        /**
         * select *
         * from Member
         * where id = :id
         */

        var sql = String.format("SELECT * FROM %s WHERE id = :id", TABLE_NAME);

        var param = new MapSqlParameterSource()
                .addValue("id", id);

        var member = namedParameterJdbcTemplate.queryForObject(sql, param, rowMapper);

        return Optional.ofNullable(member);
    }

    public List<Member> findAllByIdIn(List<Long> ids) {

        //List 를 넘겨줄 땐 항상 빈 리스트를 고려해야 한다.
        if(ids.isEmpty()) //빈 리스트를 넘겨주면 bad sql grammar 에러가 발생함
            return List.of();

        var sql = String.format("SELECT * FROM %s WHERE id IN (:ids)", TABLE_NAME);
        var param = new MapSqlParameterSource()
                .addValue("ids", ids);
        return namedParameterJdbcTemplate.query(sql, param, rowMapper);
    }

    public Member save(Member member) {
        /**
         * member id를 보고 갱신 또는 삽입을 정함
         * 반환값은 id를 담아서 반환한다.
         */

        if(member.getId() == null) {
            return insert(member);
        }
        return update(member);
    }

    private Member insert(Member member) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");

        SqlParameterSource params = new BeanPropertySqlParameterSource(member);
        var id = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        return Member.builder()
                .id(id)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .birthday(member.getBirthday())
                .createdAt(member.getCreatedAt())
                .build();
    }

    private Member update(Member member) {

        var sql = String.format(
                "UPDATE %s set email =:email, nickname =:nickname, birthday =:birthday, createdAt =:createdAt where id =:id",
                TABLE_NAME
        );

        SqlParameterSource params = new BeanPropertySqlParameterSource(member);

        namedParameterJdbcTemplate.update(sql, params);

        return member;
    }
}
