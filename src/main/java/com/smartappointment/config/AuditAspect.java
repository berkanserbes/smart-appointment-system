package com.smartappointment.config;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.smartappointment.service.interfaces.IAuditService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class AuditAspect {

    private static final int MAX_DETAILS_LENGTH = 2000;
    private static final ThreadLocal<Integer> CALL_DEPTH = ThreadLocal.withInitial(() -> 0);

    private final IAuditService auditService;

    public AuditAspect(IAuditService auditService) {
        this.auditService = auditService;
    }

    @Around("execution(public * com.smartappointment.service.impl..*(..)) && !execution(* com.smartappointment.service.impl.AuditService.*(..))")
    public Object auditServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        int depth = CALL_DEPTH.get();
        CALL_DEPTH.set(depth + 1);

        boolean topLevelCall = depth == 0;
        Instant startedAt = Instant.now();

        try {
            Object result = joinPoint.proceed();

            if (topLevelCall) {
                saveAuditSuccess(joinPoint, result, startedAt);
            }

            return result;
        } catch (Throwable throwable) {
            if (topLevelCall) {
                saveAuditFailure(joinPoint, throwable, startedAt);
            }
            throw throwable;
        } finally {
            int updatedDepth = CALL_DEPTH.get() - 1;
            if (updatedDepth <= 0) {
                CALL_DEPTH.remove();
            } else {
                CALL_DEPTH.set(updatedDepth);
            }
        }
    }

    private void saveAuditSuccess(ProceedingJoinPoint joinPoint, Object result, Instant startedAt) {
        String methodName = joinPoint.getSignature().getName();
        String action = resolveAction(methodName);
        String entityType = resolveEntityType(joinPoint);
        Long entityId = resolveEntityId(joinPoint.getArgs(), result);

        long durationMs = Duration.between(startedAt, Instant.now()).toMillis();
        String details = truncate(String.format(
                "method=%s.%s, outcome=SUCCESS, durationMs=%d",
                joinPoint.getSignature().getDeclaringTypeName(),
                methodName,
                durationMs));

        auditService.logAction(
                getCurrentUserEmail(),
                action,
                entityType,
                entityId,
                details,
                getClientIpAddress());
    }

    private void saveAuditFailure(ProceedingJoinPoint joinPoint, Throwable throwable, Instant startedAt) {
        String entityType = resolveEntityType(joinPoint);
        Long entityId = resolveEntityId(joinPoint.getArgs(), null);

        long durationMs = Duration.between(startedAt, Instant.now()).toMillis();
        String details = truncate(String.format(
                "method=%s.%s, outcome=FAILURE, durationMs=%d, exception=%s, message=%s",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                durationMs,
                throwable.getClass().getSimpleName(),
                throwable.getMessage()));

        auditService.logAction(
                getCurrentUserEmail(),
                "ERROR",
                entityType,
                entityId,
                details,
                getClientIpAddress());
    }

    private String resolveAction(String methodName) {
        if (methodName == null) {
            return "EXECUTE";
        }

        String normalized = methodName.toLowerCase();

        if (normalized.startsWith("create") || normalized.startsWith("add") || normalized.startsWith("register")) {
            return "CREATE";
        }
        if (normalized.startsWith("update") || normalized.startsWith("change") || normalized.startsWith("confirm")
                || normalized.startsWith("complete") || normalized.startsWith("reject")
                || normalized.startsWith("mark") || normalized.startsWith("activate")
                || normalized.startsWith("deactivate") || normalized.startsWith("cancel")) {
            return "UPDATE";
        }
        if (normalized.startsWith("delete") || normalized.startsWith("remove")) {
            return "DELETE";
        }
        if (normalized.startsWith("get") || normalized.startsWith("find") || normalized.startsWith("count")
                || normalized.startsWith("exists") || normalized.startsWith("search")) {
            return "READ";
        }

        return "EXECUTE";
    }

    private String resolveEntityType(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        if (className.endsWith("Service")) {
            return className.substring(0, className.length() - "Service".length()).toUpperCase();
        }
        return className.toUpperCase();
    }

    private Long resolveEntityId(Object[] args, Object result) {
        Long fromArgs = resolveEntityIdFromArgs(args);
        if (fromArgs != null) {
            return fromArgs;
        }

        return resolveEntityIdFromObject(result);
    }

    private Long resolveEntityIdFromArgs(Object[] args) {
        if (args == null) {
            return null;
        }

        for (Object arg : args) {
            if (arg instanceof Long longValue) {
                return longValue;
            }
            if (arg instanceof Integer integerValue) {
                return integerValue.longValue();
            }

            Long reflectedId = resolveEntityIdFromObject(arg);
            if (reflectedId != null) {
                return reflectedId;
            }
        }

        return null;
    }

    private Long resolveEntityIdFromObject(Object object) {
        if (object == null) {
            return null;
        }

        try {
            Method getIdMethod = object.getClass().getMethod("getId");
            Object idValue = getIdMethod.invoke(object);
            return toLong(idValue);
        } catch (ReflectiveOperationException | SecurityException ignored) {
            // continue with alternative reflection path
        }

        try {
            Method idMethod = object.getClass().getMethod("id");
            Object idValue = idMethod.invoke(object);
            return toLong(idValue);
        } catch (ReflectiveOperationException | SecurityException ignored) {
            return null;
        }
    }

    private Long toLong(Object value) {
        if (value instanceof Long longValue) {
            return longValue;
        }
        if (value instanceof Integer intValue) {
            return intValue.longValue();
        }
        if (value instanceof Number numberValue) {
            return numberValue.longValue();
        }
        return null;
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String name = authentication.getName();
        if (name == null || "anonymousUser".equalsIgnoreCase(name)) {
            return null;
        }

        return name;
    }

    private String getClientIpAddress() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes servletAttributes)) {
            return null;
        }

        HttpServletRequest request = servletAttributes.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private String truncate(String details) {
        if (details == null || details.length() <= MAX_DETAILS_LENGTH) {
            return details;
        }

        log.debug("Audit details truncated from {} to {} characters", details.length(), MAX_DETAILS_LENGTH);
        return details.substring(0, MAX_DETAILS_LENGTH);
    }
}
