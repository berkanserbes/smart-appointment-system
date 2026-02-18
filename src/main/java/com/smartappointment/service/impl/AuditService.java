package com.smartappointment.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.smartappointment.dto.audit.responses.AuditLogResponse;
import com.smartappointment.exception.BadRequestException;
import com.smartappointment.mapper.AuditLogMapper;
import com.smartappointment.model.entity.AuditLog;
import com.smartappointment.model.entity.User;
import com.smartappointment.repository.AuditLogRepository;
import com.smartappointment.repository.UserRepository;
import com.smartappointment.service.interfaces.IAuditService;

import lombok.extern.slf4j.Slf4j;

/**
 * Audit logging service.
 *
 * This service is called by AOP (AuditAspect).
 * Every important operation (CRUD) is automatically recorded in the audit_logs
 * table.
 */
@Slf4j
@Service
public class AuditService implements IAuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AuditLogMapper auditLogMapper;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository,
            AuditLogMapper auditLogMapper) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(String userEmail, String action, String entityType, Long entityId, String details,
            String ipAddress) {
        String normalizedAction = normalize(action);
        String normalizedEntityType = normalize(entityType);

        if (isBlank(normalizedAction) || isBlank(normalizedEntityType)) {
            log.warn("Skipped audit log due to missing mandatory fields: action='{}', entityType='{}'",
                    action, entityType);
            return;
        }

        try {
            User user = resolveUserByEmail(normalize(userEmail));

            AuditLog auditLog = auditLogMapper.toEntity(
                    user,
                    normalizedAction,
                    normalizedEntityType,
                    entityId,
                    normalize(details),
                    normalize(ipAddress));

            auditLogRepository.save(auditLog);
            log.debug("Audit log saved: action={}, entityType={}, entityId={}, userEmail={}, ip={}",
                    normalizedAction, normalizedEntityType, entityId, normalize(userEmail), normalize(ipAddress));

        } catch (Exception e) {
            log.error("Failed to save audit log: action={}, entityType={}, entityId={}, userEmail={}",
                    normalizedAction, normalizedEntityType, entityId, normalize(userEmail), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogsByUserId(Long userId) {
        return auditLogMapper.toResponseList(auditLogRepository.findByUserIdOrderByTimestampDesc(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogsByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return auditLogMapper.toResponseList(
                auditLogRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(userId, start, end));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogsByEntityTypeAndEntityId(String entityType, Long entityId) {
        return auditLogMapper.toResponseList(
                auditLogRepository.findByEntityTypeAndEntityId(normalize(entityType), entityId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogsByEntityType(String entityType) {
        return auditLogMapper.toResponseList(
                auditLogRepository.findByEntityTypeOrderByTimestampDesc(normalize(entityType)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogsByAction(String action) {
        return auditLogMapper.toResponseList(
                auditLogRepository.findByActionOrderByTimestampDesc(normalize(action)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return auditLogMapper.toResponseList(auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end));
    }

    @Override
    @Transactional
    public void deleteOldLogs(LocalDateTime before) {
        if (before == null) {
            throw new BadRequestException("'before' date is required");
        }

        long deletedCount = auditLogRepository.deleteByTimestampBefore(before);
        log.info("Deleted {} audit logs before {}", deletedCount, before);
    }

    private User resolveUserByEmail(String userEmail) {
        if (isBlank(userEmail)) {
            return null;
        }

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            log.debug("Audit user not found for email: {}", userEmail);
        }

        return user;
    }

    private void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new BadRequestException("Start and end date are required");
        }

        if (end.isBefore(start)) {
            throw new BadRequestException("End date cannot be before start date");
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
