package com.Marketplace_Management.Shared.DTOs.Responses;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaginatedResponse<T> implements Serializable {
    private List<T> data;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    private PaginatedResponse(List<T> data, int currentPage, int pageSize, long totalElements) {
        this.data = data;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;
    }

    /** One page of results; totalPages / hasNext / hasPrevious are derived. currentPage is 0-based. */
    public static <T> PaginatedResponse<T> of(List<T> data, int currentPage, int pageSize, long totalElements) {
        return new PaginatedResponse<>(data, currentPage, pageSize, totalElements);
    }

    /** Same page with every item converted. */
    public <R> PaginatedResponse<R> map(Function<? super T, ? extends R> mapper) {
        return of(data.stream().<R>map(mapper).toList(), currentPage, pageSize, totalElements);
    }
}
