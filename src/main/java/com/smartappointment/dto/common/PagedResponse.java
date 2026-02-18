package com.smartappointment.dto.common;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * Generic paginated response wrapper.
 * {@code page} is 1-based (as seen by the client).
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last) {

    /**
     * Builds a {@link PagedResponse} from a Spring {@link Page} and the
     * 1-based page number that was originally requested by the client.
     */
    public static <T> PagedResponse<T> of(Page<T> springPage, int requestedPage) {
        return new PagedResponse<>(
                springPage.getContent(),
                requestedPage,
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.isLast());
    }
}
