package io.github.mugishajc.library.member;

import io.github.mugishajc.library.loan.LoanService;
import io.github.mugishajc.library.loan.dto.LoanResponse;
import io.github.mugishajc.library.member.dto.MemberRequest;
import io.github.mugishajc.library.member.dto.MemberResponse;
import io.github.mugishajc.library.member.dto.MemberStatusRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final LoanService loanService;

    public MemberController(MemberService memberService, LoanService loanService) {
        this.memberService = memberService;
        this.loanService = loanService;
    }

    @GetMapping
    public Page<MemberResponse> list(@PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return memberService.list(pageable);
    }

    @GetMapping("/{id}")
    public MemberResponse get(@PathVariable Long id) {
        return memberService.get(id);
    }

    @PostMapping
    public ResponseEntity<MemberResponse> register(@Valid @RequestBody MemberRequest request) {
        MemberResponse created = memberService.register(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public MemberResponse update(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return memberService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public MemberResponse changeStatus(@PathVariable Long id, @Valid @RequestBody MemberStatusRequest request) {
        return memberService.changeStatus(id, request.status());
    }

    @GetMapping("/{id}/loans")
    public Page<LoanResponse> loans(@PathVariable Long id,
                                    @PageableDefault(size = 20, sort = "borrowedOn", direction = Sort.Direction.DESC)
                                    Pageable pageable) {
        return loanService.forMember(id, pageable);
    }
}
