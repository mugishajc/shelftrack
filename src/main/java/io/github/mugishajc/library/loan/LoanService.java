package io.github.mugishajc.library.loan;

import io.github.mugishajc.library.book.Book;
import io.github.mugishajc.library.book.BookRepository;
import io.github.mugishajc.library.common.BusinessRuleException;
import io.github.mugishajc.library.common.NotFoundException;
import io.github.mugishajc.library.loan.dto.LoanResponse;
import io.github.mugishajc.library.member.Member;
import io.github.mugishajc.library.member.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

@Service
public class LoanService {

    private final LoanRepository loans;
    private final BookRepository books;
    private final MemberRepository members;
    private final LoanPolicy policy;
    private final FineCalculator fines;
    private final Clock clock;

    public LoanService(LoanRepository loans, BookRepository books, MemberRepository members,
                       LoanPolicy policy, FineCalculator fines, Clock clock) {
        this.loans = loans;
        this.books = books;
        this.members = members;
        this.policy = policy;
        this.fines = fines;
        this.clock = clock;
    }

    @Transactional
    public LoanResponse borrow(Long bookId, Long memberId) {
        Member member = members.findById(memberId).orElseThrow(() -> new NotFoundException("Member", memberId));
        Book book = books.findById(bookId).orElseThrow(() -> new NotFoundException("Book", bookId));
        LocalDate today = today();

        if (!member.isActive()) {
            throw new BusinessRuleException("Member is suspended");
        }
        if (loans.existsByMemberIdAndReturnedOnIsNullAndDueOnBefore(memberId, today)) {
            throw new BusinessRuleException("Member has overdue books and must return them first");
        }
        if (loans.countByMemberIdAndReturnedOnIsNull(memberId) >= policy.maxActiveLoans()) {
            throw new BusinessRuleException("Member already has " + policy.maxActiveLoans() + " books on loan");
        }
        if (loans.existsByBookIdAndMemberIdAndReturnedOnIsNull(bookId, memberId)) {
            throw new BusinessRuleException("Member already has a copy of this book");
        }
        if (!book.hasAvailableCopy()) {
            throw new BusinessRuleException("No copies of '" + book.getTitle() + "' are available");
        }

        book.checkOut();
        Loan loan = loans.save(new Loan(book, member, today, today.plusDays(policy.loanDays())));
        return toResponse(loan, today);
    }

    @Transactional
    public LoanResponse returnBook(Long loanId) {
        Loan loan = find(loanId);
        if (loan.isReturned()) {
            throw new BusinessRuleException("Loan " + loanId + " was already returned on " + loan.getReturnedOn());
        }
        LocalDate today = today();
        loan.markReturned(today, fines.fineFor(loan.getDueOn(), today));
        loan.getBook().checkIn();
        return toResponse(loan, today);
    }

    /** Extends the due date by another loan period, counted from today. */
    @Transactional
    public LoanResponse renew(Long loanId) {
        Loan loan = find(loanId);
        LocalDate today = today();
        if (loan.isReturned()) {
            throw new BusinessRuleException("Returned loans cannot be renewed");
        }
        if (loan.isOverdue(today)) {
            throw new BusinessRuleException("Overdue loans cannot be renewed; return the book first");
        }
        if (loan.getRenewals() >= policy.maxRenewals()) {
            throw new BusinessRuleException("Loan has reached the renewal limit of " + policy.maxRenewals());
        }
        loan.extendTo(today.plusDays(policy.loanDays()));
        return toResponse(loan, today);
    }

    @Transactional(readOnly = true)
    public LoanResponse get(Long loanId) {
        return toResponse(find(loanId), today());
    }

    @Transactional(readOnly = true)
    public Page<LoanResponse> list(LoanStatus status, Pageable pageable) {
        LocalDate today = today();
        Page<Loan> page;
        if (status == null) {
            page = loans.findAll(pageable);
        } else {
            page = switch (status) {
                case ACTIVE -> loans.findByReturnedOnIsNull(pageable);
                case OVERDUE -> loans.findOverdue(today, pageable);
                case RETURNED -> loans.findByReturnedOnIsNotNull(pageable);
            };
        }
        return page.map(loan -> toResponse(loan, today));
    }

    @Transactional(readOnly = true)
    public Page<LoanResponse> forMember(Long memberId, Pageable pageable) {
        if (!members.existsById(memberId)) {
            throw new NotFoundException("Member", memberId);
        }
        LocalDate today = today();
        return loans.findByMemberId(memberId, pageable).map(loan -> toResponse(loan, today));
    }

    private Loan find(Long loanId) {
        return loans.findById(loanId).orElseThrow(() -> new NotFoundException("Loan", loanId));
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }

    private LoanResponse toResponse(Loan loan, LocalDate today) {
        BigDecimal fine = loan.isReturned() ? loan.getFineAmount() : fines.fineFor(loan.getDueOn(), today);
        return new LoanResponse(loan.getId(), loan.getBook().getId(), loan.getBook().getTitle(),
                loan.getMember().getId(), loan.getMember().getFullName(), loan.getBorrowedOn(), loan.getDueOn(),
                loan.getReturnedOn(), loan.getRenewals(), loan.statusOn(today), fine);
    }
}
