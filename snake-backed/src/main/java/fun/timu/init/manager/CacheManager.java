package fun.timu.init.manager;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CacheManager {

    private final StringRedisTemplate stringRedisTemplate;
    // 本地缓存
    private final Cache<String, String> LOCAL_CACHE = Caffeine.newBuilder().initialCapacity(1024).maximumSize(10000L).expireAfterWrite(5L, TimeUnit.MINUTES).build(); // 缓存 5 分钟移除

    public CacheManager(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 从缓存中获取数据
     *
     * @param cacheKey 缓存键
     * @param clazz    数据类型
     * @param <T>      泛型类型
     * @return 缓存中的数据
     */
    public <T> T getFromCache(String cacheKey, Class<T> clazz) {
        // 1. 查询本地缓存（Caffeine）
        String cachedValue = (String) LOCAL_CACHE.getIfPresent(cacheKey);
        if (cachedValue != null) {
            if ("NULL".equals(cachedValue)) {
                // 如果缓存的是空对象标记，直接返回空结果
                return null;
            }
            return JSONUtil.toBean(cachedValue, clazz);
        }

        // 2. 查询分布式缓存（Redis）
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        cachedValue = valueOps.get(cacheKey);
        if (cachedValue != null) {
            if ("NULL".equals(cachedValue)) {
                return null; // 如果缓存的是空对象标记，直接返回空结果
            }
            // 如果命中 Redis，存入本地缓存并返回
            LOCAL_CACHE.put(cacheKey, cachedValue);
            return JSONUtil.toBean(cachedValue, clazz);
        }

        return null;
    }

    /**
     * 将数据存入缓存
     *
     * @param cacheKey 缓存键
     * @param value    数据
     */
    public void putToCache(String cacheKey, Object value) {
        String cacheValue;
        if (value == null) {
            // 如果查询结果为空，缓存一个空对象标记
            cacheValue = "NULL";
        } else {
            cacheValue = JSONUtil.toJsonStr(value);
        }
        // 更新本地缓存
        LOCAL_CACHE.put(cacheKey, cacheValue);
        // 更新 Redis 缓存，设置过期时间为 5 分钟到 10 分钟之间
        int cacheExpireTime = 300 + RandomUtil.randomInt(0, 300);
        stringRedisTemplate.opsForValue().set(cacheKey, cacheValue, cacheExpireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取分布式锁
     *
     * @param lockKey 锁键
     * @return 是否获取到锁
     */
    public boolean acquireLock(String lockKey) {
        return stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey 锁键
     */
    public void releaseLock(String lockKey) {
        stringRedisTemplate.delete(lockKey);
    }
}
