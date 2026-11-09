package io.github.mugishajc.library.book;

import io.github.mugishajc.library.book.dto.BookRequest;
import io.github.mugishajc.library.book.dto.BookResponse;
import io.github.mugishajc.library.common.BusinessRuleException;
import io.github.mugishajc.library.common.ConflictException;
import io.github.mugishajc.library.common.NotFoundException;
import io.github.mugishajc.library.loan.LoanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BookService {

    private final BookRepository books;
    private final LoanRepository loans;

    public BookService(BookRepository books, LoanRepository loans) {
        this.books = books;
        this.loans = loans;
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> list(String query, boolean availableOnly, Pageable pageable) {
        Page<Book> page;
        if (StringUtils.hasText(query)) {
            page = books.search(query.strip(), pageable);
        } else if (availableOnly) {
            page = books.findByAvailableCopiesGreaterThan(0, pageable);
        } else {
            page = books.findAll(pageable);
        }
        return page.map(BookResponse::from);
    }

    @Transactional(readOnly = true)
    public BookResponse get(Long id) {
        return BookResponse.from(find(id));
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        String isbn = validIsbn(request.isbn());
        if (books.existsByIsbn(isbn)) {
            throw new ConflictException("A book with ISBN " + isbn + " already exists");
        }
        Book book = new Book(isbn, request.title().strip(), request.author().strip(),
                request.publishedYear(), request.totalCopies());
        return BookResponse.from(books.save(book));
    }

    /** ISBN is treated as the book's identity and cannot be changed after creation. */
    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = find(id);
        if (!book.getIsbn().equals(Isbn.normalise(request.isbn()))) {
            throw new BusinessRuleException("ISBN cannot be changed; create a new book instead");
        }
        book.updateDetails(request.title().strip(), request.author().strip(), request.publishedYear());
        try {
            book.changeTotalCopies(request.totalCopies());
        } catch (IllegalStateException e) {
            throw new BusinessRuleException(e.getMessage());
        }
        return BookResponse.from(book);
    }

    @Transactional
    public void delete(Long id) {
        Book book = find(id);
        if (loans.existsByBookIdAndReturnedOnIsNull(id)) {
            throw new BusinessRuleException("Book has copies on loan and cannot be removed");
        }
        books.delete(book);
    }

    Book find(Long id) {
        return books.findById(id).orElseThrow(() -> new NotFoundException("Book", id));
    }

    private static String validIsbn(String raw) {
        if (!Isbn.isValid(raw)) {
            throw new BusinessRuleException("'" + raw + "' is not a valid ISBN-10 or ISBN-13");
        }
        return Isbn.normalise(raw);
    }
}
