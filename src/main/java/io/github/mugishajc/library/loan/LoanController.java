package io.github.mugishajc.library.loan;

import io.github.mugishajc.library.loan.dto.BorrowRequest;
import io.github.mugishajc.library.loan.dto.LoanResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public Page<LoanResponse> list(@RequestParam(required = false) LoanStatus status,
                                   @PageableDefault(size = 20, sort = "dueOn", direction = Sort.Direction.ASC)
                                   Pageable pageable) {
        return loanService.list(status, pageable);
    }

    @GetMapping("/{id}")
    public LoanResponse get(@PathVariable Long id) {
        return loanService.get(id);
    }

    @PostMapping
    public ResponseEntity<LoanResponse> borrow(@Valid @RequestBody BorrowRequest request) {
        LoanResponse loan = loanService.borrow(request.bookId(), request.memberId());
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(loan.id()).toUri();
        return ResponseEntity.created(location).body(loan);
    }

    @PostMapping("/{id}/return")
    public LoanResponse returnBook(@PathVariable Long id) {
        return loanService.returnBook(id);
    }

    @PostMapping("/{id}/renew")
    public LoanResponse renew(@PathVariable Long id) {
        return loanService.renew(id);
    }
}
