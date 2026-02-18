package com.smartappointment.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Utility for building Spring {@link Pageable} objects from client-facing
 * 1-based page numbers.
 *
 * <p>The client always sends {@code page=1} to request the first page.
 * Internally Spring Data uses 0-based page indices, so this helper
 * performs the conversion transparently.</p>
 */
public final class PaginationUtils {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private PaginationUtils() {
    }

    /**
     * Converts client-supplied 1-based {@code page} and {@code pageSize}
     * into a Spring {@link Pageable}.
     *
     * @param page     1-based page number (values &lt; 1 are treated as 1)
     * @param pageSize number of items per page (values &lt; 1 default to 20;
     *                 values &gt; 100 are capped at 100)
     * @return a Spring {@link PageRequest} ready for repository calls
     */
    public static Pageable toPageable(int page, int pageSize) {
        int zeroBasedPage = Math.max(0, page - 1);
        int size = pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        return PageRequest.of(zeroBasedPage, size);
    }
}
