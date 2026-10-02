package com.ganpat.spendlyticsbackend.service;

import java.util.Set;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExpenseCacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;

    public ExpenseCacheService(
            RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void evictUserSummery(Long userId) {

        String pattern = "expenseSummery::" + userId + ":*";

        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);

            System.out.println(
                "Deleted summary cache keys: " + keys
            );
        } else {
            System.out.println(
                "No summary cache found for user: " + userId
            );
        }
    }
}