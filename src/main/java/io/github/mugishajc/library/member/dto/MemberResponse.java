package io.github.mugishajc.library.member.dto;

import io.github.mugishajc.library.member.Member;
import io.github.mugishajc.library.member.MemberStatus;

import java.time.LocalDate;

public record MemberResponse(Long id, String fullName, String email, String phone,
                             MemberStatus status, LocalDate joinedOn) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getFullName(), member.getEmail(), member.getPhone(),
                member.getStatus(), member.getJoinedOn());
    }
}
