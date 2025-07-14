package com.phraiz.back.common.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String DAILY_USAGE_PREFIX = "daily_usage:";
    private static final String MONTHLY_USAGE_PREFIX = "monthly_usage:";


    @PostConstruct
    public void logRedisConnectionInfo() {
        try {
            // 실제 Redis 커넥션 팩토리에서 host 정보 확인
            Object connectionFactory = redisTemplate.getConnectionFactory();
            if (connectionFactory instanceof org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory factory) {
                log.info("🔍 Redis 연결 정보 - host: {}, port: {}", factory.getHostName(), factory.getPort());
            }
        } catch (Exception e) {
            log.error("❌ Redis 연결 정보 확인 실패", e);
        }
    }

    /** ================================
     *  일별 사용량 관련 메서드
     *  ================================ */

    public void incrementDailyUsage(String memberId, String dateKey, long increment, Duration ttl) {
        String key = DAILY_USAGE_PREFIX + memberId + ":" + dateKey;
        Long result = redisTemplate.opsForValue().increment(key, increment);

        if (result != null && result == increment) {
            // 처음 생성된 경우에만 TTL 설정
            redisTemplate.expire(key, ttl);
        }
    }

    public long getDailyUsage(String memberId, String dateKey) {
        String key = DAILY_USAGE_PREFIX + memberId + ":" + dateKey;
        String val = redisTemplate.opsForValue().get(key);
        return val != null ? Long.parseLong(val) : 0L;
    }

    public void resetDailyUsage(String memberId, String dateKey) {
        String key = DAILY_USAGE_PREFIX + memberId + ":" + dateKey;
        redisTemplate.delete(key);
    }

    /** ================================
     *  월별 사용량 관련 메서드
     *  ================================ */

    public void incrementMonthlyUsage(String memberId, String monthKey, long increment) {
        String key = MONTHLY_USAGE_PREFIX + memberId + ":" + monthKey;
        Long result = redisTemplate.opsForValue().increment(key, increment);

        if (result == null) {
            throw new IllegalStateException("🚨 Redis increment 결과가 null입니다. key=" + key);
        }

        // key가 새로 생성된 경우 → result == increment
        boolean isNewKey = (result == increment);

        if (isNewKey) {
            redisTemplate.expire(key, ttlUntilEndOfMonth());
        }
    }

    public void setMonthlyUsage(String memberId, String monthKey, Long value) {
        if (value == null) {
            log.warn("🚨 월 사용량 값이 null 입니다. 0으로 초기화합니다.");
            value = 0L;
        }
        String key = MONTHLY_USAGE_PREFIX + memberId + ":" + monthKey;
        redisTemplate.opsForValue().set(key, String.valueOf(value), ttlUntilEndOfMonth());
    }


    public long getMonthlyUsage(String memberId, String monthKey) {
        String key = MONTHLY_USAGE_PREFIX + memberId + ":" + monthKey;
        String val = redisTemplate.opsForValue().get(key);

        if (val == null) {
            return -1;
        }
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            /**
             * Todo.
             * Redis에 저장된 사용량 값이 잘못되었습니다 예외 던지기
             */
            log.error("🚨 Redis에 저장된 사용량 값이 잘못되었습니다. key={}, value={}", key, val);
            return 0L; // 또는 throw new IllegalStateException(...)로 바꿔도 됩니다.
        }
    }

    public void resetMonthlyUsage(String memberId, String monthKey) {
        String key = MONTHLY_USAGE_PREFIX + memberId + ":" + monthKey;
        redisTemplate.delete(key);
    }

    private Duration ttlUntilEndOfMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate firstDayNextMonth = now.toLocalDate().plusMonths(1).withDayOfMonth(1);
        LocalDateTime endOfMonth = firstDayNextMonth.atStartOfDay().minusSeconds(1);

        return Duration.between(now, endOfMonth);
    }

}