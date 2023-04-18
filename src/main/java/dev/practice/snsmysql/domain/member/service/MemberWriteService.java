package dev.practice.snsmysql.domain.member.service;

import dev.practice.snsmysql.domain.member.dto.RegisterMemberCommand;
import dev.practice.snsmysql.domain.member.entity.Member;
import dev.practice.snsmysql.domain.member.entity.MemberNicknameHistory;
import dev.practice.snsmysql.domain.member.repository.MemberNicknameHistoryRepository;
import dev.practice.snsmysql.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberWriteService {

    private final MemberRepository memberRepository;
    private final MemberNicknameHistoryRepository memberNicknameHistoryRepository;

    @Transactional
    public Member register(RegisterMemberCommand command) {
        /**
         * 목표 - 회원정보(이메일, 닉네임, 생년월일)를 등록한다.
         *     - 닉네임은 10자를 넘길 수 없다.
         *
         * 파라미터 - memberRegisterCommand
         *
         * val member = Member.of(memberRegisterCommand)
         *
         * memberRepository.save(member)
         *
         * 해당 로직은 정합성을 지켜야 하기 때문에 트랜잭션 처리가 필요하다.
         * Insert 쿼리 2개가 실행된다.
         */

        var member = Member.builder()
                .nickname(command.nickname())
                .email(command.email())
                .birthday(command.birthday())
                .build();

        var savedMember = memberRepository.save(member);
        saveMemberNicknameHistory(savedMember); //최초 이력 저장

        return savedMember;
    }

    @Transactional
    public void changeNickname(Long memberId, String nickname) {
        /**
         * 목표 - 회원의 닉네임을 변경한다.
         * 1. 회원의 이름을 변경
         * 2. 변경 내역(바꾼 이름)을 저장한다.
         */

        var member = memberRepository.findById(memberId).orElseThrow();
        member.changeNickname(nickname);

        saveMemberNicknameHistory(member);
    }

    private void saveMemberNicknameHistory(Member member) {
        var memberNicknameHistory = MemberNicknameHistory.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();
        memberNicknameHistoryRepository.save(memberNicknameHistory);
    }
}
