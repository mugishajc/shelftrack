package io.github.mugishajc.library.loan;

import io.github.mugishajc.library.book.Book;
import io.github.mugishajc.library.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "borrowed_on", nullable = false, updatable = false)
    private LocalDate borrowedOn;

    @Column(name = "due_on", nullable = false)
    private LocalDate dueOn;

    @Column(name = "returned_on")
    private LocalDate returnedOn;

    @Column(nullable = false)
    private int renewals;

    @Column(name = "fine_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal fineAmount = BigDecimal.ZERO;

    @Version
    private long version;

    protected Loan() {
    }

    public Loan(Book book, Member member, LocalDate borrowedOn, LocalDate dueOn) {
        this.book = book;
        this.member = member;
        this.borrowedOn = borrowedOn;
        this.dueOn = dueOn;
    }

    public boolean isReturned() {
        return returnedOn != null;
    }

    public boolean isOverdue(LocalDate today) {
        return !isReturned() && today.isAfter(dueOn);
    }

    public LoanStatus statusOn(LocalDate today) {
        if (isReturned()) {
            return LoanStatus.RETURNED;
        }
        return isOverdue(today) ? LoanStatus.OVERDUE : LoanStatus.ACTIVE;
    }

    void markReturned(LocalDate on, BigDecimal fine) {
        this.returnedOn = on;
        this.fineAmount = fine;
    }

    void extendTo(LocalDate newDueDate) {
        this.dueOn = newDueDate;
        this.renewals++;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getBorrowedOn() {
        return borrowedOn;
    }

    public LocalDate getDueOn() {
        return dueOn;
    }

    public LocalDate getReturnedOn() {
        return returnedOn;
    }

    public int getRenewals() {
        return renewals;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }
}
