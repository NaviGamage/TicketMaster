package edu.icet.Aspect;

import edu.icet.Model.Entity.AuditLog;
import edu.icet.Repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    @AfterThrowing(
            pointcut = "@annotation(edu.icet.Annotation.AuditFailure)",
            throwing = "exception"
    )
    public void auditFailure(JoinPoint joinPoint, Exception exception) {
        try {
            // Extract user ID from method arguments if present
            Long userId = extractUserId(joinPoint.getArgs());

            // Get method name
            String methodName = joinPoint.getSignature().getName();

            // Create audit log entry
            AuditLog auditLog = AuditLog.builder()
                    .action("BOOKING_FAILURE")
                    .userId(userId)
                    .details(String.format("Method: %s, Exception: %s, Message: %s",
                            methodName,
                            exception.getClass().getSimpleName(),
                            exception.getMessage()))
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);

            log.info("Audit log created for failed booking - User ID: {}, Reason: {}",
                    userId, exception.getMessage());

        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }

    private Long extractUserId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        return null;
    }
}