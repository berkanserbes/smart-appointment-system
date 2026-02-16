package com.smartappointment.service.interfaces;

import com.smartappointment.dto.audit.AuditLogResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface IAuditService {

    void logAction(String userEmail, String action, String entityType, Long entityId, String details, String ipAddress);

    List<AuditLogResponse> getLogsByUserId(Long userId);

    List<AuditLogResponse> getLogsByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);

    List<AuditLogResponse> getLogsByEntityTypeAndEntityId(String entityType, Long entityId);

    List<AuditLogResponse> getLogsByEntityType(String entityType);

    List<AuditLogResponse> getLogsByAction(String action);

    List<AuditLogResponse> getLogsByDateRange(LocalDateTime start, LocalDateTime end);

    void deleteOldLogs(LocalDateTime before);
}
