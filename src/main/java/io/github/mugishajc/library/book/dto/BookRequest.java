package io.github.mugishajc.library.book.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @NotBlank @Size(max = 17) String isbn,
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 255) String author,
        @Min(1450) @Max(2100) Integer publishedYear,
        @NotNull @Min(1) @Max(1000) Integer totalCopies) {
}
