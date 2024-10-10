package com.nemo.oceanAcademy.common.aop;

import com.nemo.oceanAcademy.common.log.dataAccess.entity.ExecutionTime;
import com.nemo.oceanAcademy.common.log.dataAccess.repository.ExecutionTimeRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Aspect
@Component
@RequiredArgsConstructor
public class TimeTraceAop {

    private final ExecutionTimeRepository executionTimeRepository;

    @Around("execution(* com.nemo.oceanAcademy.domain..*(..))") // 패키지 하위에 모두 적용
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed(); // 다음 로직으로 넘어간다.
        } finally {
            long finish = System.currentTimeMillis();
            long executionTime = finish - start;
            ExecutionTime ex = ExecutionTime.builder()
                    .methodName(joinPoint.getSignature().getDeclaringTypeName()
                            + "." + joinPoint.getSignature().getName())
                    .executionTime(executionTime)
                    .executedAt(LocalDateTime.now())
                    .startedAt(LocalDateTime.ofEpochSecond(start / 1000, 0, ZoneOffset.UTC)) // 수정된 부분
                    .build();
            executionTimeRepository.save(ex);
        }
    }
}
