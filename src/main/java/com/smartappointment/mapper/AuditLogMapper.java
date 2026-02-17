package com.smartappointment.mapper;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.audit.responses.AuditLogResponse;
import com.smartappointment.model.entity.AuditLog;
import com.smartappointment.model.entity.User;

@Component
public class AuditLogMapper {

    public AuditLog toEntity(User user, String action, String entityType, Long entityId, String details,
            String ipAddress) {
        return AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .ipAddress(ipAddress)
                .build();
    }

    public AuditLogResponse toResponse(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }

        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getUser() != null ? auditLog.getUser().getId() : null,
                auditLog.getUser() != null ? auditLog.getUser().getEmail() : null,
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getDetails(),
                auditLog.getIpAddress(),
                auditLog.getTimestamp());
    }
}
