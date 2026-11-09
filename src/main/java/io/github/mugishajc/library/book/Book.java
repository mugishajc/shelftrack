package io.github.mugishajc.library.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(name = "published_year")
    private Integer publishedYear;

    @Column(name = "total_copies", nullable = false)
    private int totalCopies;

    @Column(name = "available_copies", nullable = false)
    private int availableCopies;

    // Two librarians lending the last copy at the same moment must not both succeed.
    @Version
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Book() {
    }

    public Book(String isbn, String title, String author, Integer publishedYear, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public int copiesOnLoan() {
        return totalCopies - availableCopies;
    }

    public boolean hasAvailableCopy() {
        return availableCopies > 0;
    }

    public void checkOut() {
        if (availableCopies == 0) {
            throw new IllegalStateException("No copies of '" + title + "' are available");
        }
        availableCopies--;
    }

    public void checkIn() {
        if (availableCopies == totalCopies) {
            throw new IllegalStateException("All copies of '" + title + "' are already on the shelf");
        }
        availableCopies++;
    }

    /** Changes the stock level while keeping the number of copies on loan unchanged. */
    public void changeTotalCopies(int newTotal) {
        int onLoan = copiesOnLoan();
        if (newTotal < onLoan) {
            throw new IllegalStateException(onLoan + " copies are on loan; total cannot drop to " + newTotal);
        }
        this.totalCopies = newTotal;
        this.availableCopies = newTotal - onLoan;
    }

    public void updateDetails(String title, String author, Integer publishedYear) {
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
    }

    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
