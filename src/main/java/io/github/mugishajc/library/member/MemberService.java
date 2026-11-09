package io.github.mugishajc.library.member;

import io.github.mugishajc.library.common.ConflictException;
import io.github.mugishajc.library.common.NotFoundException;
import io.github.mugishajc.library.member.dto.MemberRequest;
import io.github.mugishajc.library.member.dto.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;

@Service
public class MemberService {

    private final MemberRepository members;
    private final Clock clock;

    public MemberService(MemberRepository members, Clock clock) {
        this.members = members;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public Page<MemberResponse> list(Pageable pageable) {
        return members.findAll(pageable).map(MemberResponse::from);
    }

    @Transactional(readOnly = true)
    public MemberResponse get(Long id) {
        return MemberResponse.from(find(id));
    }

    @Transactional
    public MemberResponse register(MemberRequest request) {
        String email = request.email().strip().toLowerCase(Locale.ROOT);
        if (members.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("A member with email " + email + " already exists");
        }
        Member member = new Member(request.fullName().strip(), email, request.phone(), LocalDate.now(clock));
        return MemberResponse.from(members.save(member));
    }

    /** Email is the member's login identity elsewhere, so only name and phone are editable. */
    @Transactional
    public MemberResponse update(Long id, MemberRequest request) {
        Member member = find(id);
        member.update(request.fullName().strip(), request.phone());
        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse changeStatus(Long id, MemberStatus status) {
        Member member = find(id);
        member.setStatus(status);
        return MemberResponse.from(member);
    }

    Member find(Long id) {
        return members.findById(id).orElseThrow(() -> new NotFoundException("Member", id));
    }
}
