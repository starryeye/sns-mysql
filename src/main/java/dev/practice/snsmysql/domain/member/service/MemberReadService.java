package dev.practice.snsmysql.domain.member.service;

import dev.practice.snsmysql.domain.member.dto.MemberDto;
import dev.practice.snsmysql.domain.member.dto.MemberNicknameHistoryDto;
import dev.practice.snsmysql.domain.member.entity.Member;
import dev.practice.snsmysql.domain.member.entity.MemberNicknameHistory;
import dev.practice.snsmysql.domain.member.repository.MemberNicknameHistoryRepository;
import dev.practice.snsmysql.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MemberReadService {

    private final MemberRepository memberRepository;
    private final MemberNicknameHistoryRepository memberNicknameHistoryRepository;

    public MemberDto getMember(Long id) {

        var member = memberRepository.findById(id).orElseThrow();
        return toDto(member);
    }

    public List<MemberDto> getMemberList(List<Long> ids) {
        return memberRepository.findAllByIdIn(ids).stream()
                .map(this::toDto)
                .toList();
    }

    public List<MemberNicknameHistoryDto> getMemberNicknameHistories(Long memberId) {
        return memberNicknameHistoryRepository.findAllByMemberId(memberId).stream()
                .map(this::toDto)
                .toList();
    }

    public MemberDto toDto(Member member) {
        return new MemberDto(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getBirthday()
        );
    }

    private MemberNicknameHistoryDto toDto(MemberNicknameHistory memberNicknameHistory) {
        return new MemberNicknameHistoryDto(
                memberNicknameHistory.getId(),
                memberNicknameHistory.getMemberId(),
                memberNicknameHistory.getNickname(),
                memberNicknameHistory.getCreatedAt()
        );
    }
}
