package com.smartappointment.dto.provider.responses;

public record ServiceProviderSummaryResponse(
        Long id,
        String name,
        String title,
        String categoryName,
        boolean active) {
}
