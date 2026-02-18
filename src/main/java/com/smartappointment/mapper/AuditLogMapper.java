package com.smartappointment.mapper;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<AuditLogResponse> toResponseList(List<AuditLog> auditLogs) {
        if (auditLogs == null) {
            return List.of();
        }

        return auditLogs.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
