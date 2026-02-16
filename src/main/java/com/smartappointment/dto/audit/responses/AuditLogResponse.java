package com.smartappointment.dto.audit.responses;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        Long userId,
        String userEmail,
        String action,
        String entityType,
        Long entityId,
        String details,
        String ipAddress,
        LocalDateTime timestamp) {
}
