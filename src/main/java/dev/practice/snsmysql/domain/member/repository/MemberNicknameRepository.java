package dev.practice.snsmysql.domain.member.repository;

import dev.practice.snsmysql.domain.member.entity.MemberNicknameHistory;
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
import java.util.Optional;

/**
 * TODO : JPA 로 변경
 *
 * History 데이터는 Update 기능이 없어야 한다.
 */
@Repository
@RequiredArgsConstructor
public class MemberNicknameRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String TABLE_NAME = "MemberNicknameHistory";

    public MemberNicknameHistory save(MemberNicknameHistory memberNicknameHistory) {

        if(memberNicknameHistory.getId() == null) {
            return insert(memberNicknameHistory);
        }

        throw new UnsupportedOperationException("MemberNicknameHistory 는 Update 기능을 지원하지 않습니다.");
    }

    private MemberNicknameHistory insert(MemberNicknameHistory memberNicknameHistory) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");

        SqlParameterSource params = new BeanPropertySqlParameterSource(memberNicknameHistory);
        var id = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        return MemberNicknameHistory.builder()
                .id(id)
                .memberId(memberNicknameHistory.getMemberId())
                .nickname(memberNicknameHistory.getNickname())
                .createdAt(memberNicknameHistory.getCreatedAt())
                .build();
    }
}
