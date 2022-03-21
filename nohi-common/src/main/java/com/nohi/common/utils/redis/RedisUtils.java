package com.nohi.common.utils.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 * <p>created on 2022/3/21 17:29</p>
 * @author dute7liang
 */
@Slf4j
@Configuration
public class RedisUtils {

    private static RedisTemplate<String, Object> redisTemplate;
    private static  StringRedisTemplate stringRedisTemplate;

    public RedisUtils(StringRedisTemplate stringRedisTemplate, RedisTemplate redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
        RedisUtils.stringRedisTemplate = stringRedisTemplate;
    }

    public static RedisTemplate<String, Object> getRedisTemplate() {
        return RedisUtils.redisTemplate;
    }

    public static StringRedisTemplate getStringRedisTemplate() {
        return RedisUtils.stringRedisTemplate;
    }


    /**
     * 获取 String value
     */
    public static String get(String key) {
        return getStringRedisTemplate().opsForValue().get(key);
    }

    /**
     * 设置key  String value
     */
    public static void set(String key, String value) {
        getStringRedisTemplate().opsForValue().set(key, value);
    }

    /**
     * 设置key value,过期时间单位为秒
     */
    public static void set(String key, String value, long seconds) {
        getStringRedisTemplate().opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 根据key得到 Object value
     */
    @SuppressWarnings("unchecked")
    public static<T> T getObject(String key) {
        return (T) getRedisTemplate().opsForValue().get(key);
    }

    /**
     * 设置key Object value
     */
    public static void setObject(String key, Object value) {
        getRedisTemplate().opsForValue().set(key, value);
    }

    /**
     * 设置key Object value
     * @param ttl 存活时间 单位秒
     */
    public static void setObject(String key, Object value, long ttl) {
        getRedisTemplate().opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }

    /**
     * 有序集合添加元素
     */
    public static Boolean zAdd(String key, String value, double score) {
        return getStringRedisTemplate().opsForZSet().add(key, value, score);
    }
    /**
     * 得到排序下标start,end范围内的有序集合
     */
    public static Set<ZSetOperations.TypedTuple<String>> zRange(String key, long start, long end) {
        return  getStringRedisTemplate().opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 删除zRem对应的元素
     */
    public static Long zRem(String key, String value) {
        return getStringRedisTemplate().opsForZSet().remove(key, value);
    }


    /**
     * 简单的锁,过期时间单位为秒
     * @param second 秒
     */
    public static boolean setNX(String key, String value, long second) {
        Boolean res = getStringRedisTemplate().opsForValue().setIfAbsent(key, value,second, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(res);
    }

    /**
     * NX设置value
     */
    public static boolean setNX(String key, String value, Duration duration) {
        Boolean res = getStringRedisTemplate().opsForValue().setIfAbsent(key, value, duration);
        return Boolean.TRUE.equals(res);
    }

    /**
     * NX设置对象
     * @param second 秒
     */
    public static boolean setNXObject(String key, Object value, long second) {
        Boolean res = getRedisTemplate().opsForValue().setIfAbsent(key, value,second, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(res);
    }

    /**
     * NX设置对象
     * @param duration 存活时间
     */
    public static boolean setNXObject(String key, Object value, Duration duration) {
        Boolean res = getRedisTemplate().opsForValue().setIfAbsent(key, value,duration);
        return Boolean.TRUE.equals(res);
    }

    /**
     * 删除
     */
    public static Boolean del(String key) {
        return getStringRedisTemplate().delete(key);
    }
    /**
     * 简易分布式锁,其他线程会等待timeout时间,单位为秒
     */
    public static Boolean lock(String key, long timeout) {
        long curTime = System.currentTimeMillis();
        while (true) {
            Boolean result = setNX(key, String.valueOf(curTime), timeout);
            if (result) {
                return true;
            }
            if (timeout < 1 || (System.currentTimeMillis() - curTime > timeout * 1000)) {
                return false;
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                log.warn("lock Interrupted", e);
            }
        }
    }

    /**
     * 获取redis计数器并且设置初始值以及过期时间
     */
    public static RedisAtomicInteger getAtomicInteger(String key, Integer initValue, Long seconds) {
        RedisAtomicInteger result = new RedisAtomicInteger(key, getStringRedisTemplate().getConnectionFactory(), initValue);
        result.expire(seconds, TimeUnit.SECONDS);
        return result;
    }


    /**
     * 获取redis计数器及过期时间
     */
    public static RedisAtomicInteger getAtomicInteger(String key, Long seconds) {
        RedisAtomicInteger result = new RedisAtomicInteger(key, getStringRedisTemplate().getConnectionFactory());
        result.expire(seconds, TimeUnit.SECONDS);
        return result;
    }

    /**
     * 获取redis计数器
     */
    public static RedisAtomicInteger getAtomicInteger(String key) {
        return new RedisAtomicInteger(key, getStringRedisTemplate().getConnectionFactory());
    }

    /**
     * 获取redis计数器
     */
    public static RedisAtomicLong getRedisAtomicLong(String key, Long seconds) {
        RedisAtomicLong result = new RedisAtomicLong(key, getStringRedisTemplate().getConnectionFactory());
        result.expire(seconds, TimeUnit.SECONDS);
        return result;
    }

    /**
     * 获取redis计数器
     */
    public static RedisAtomicLong getRedisAtomicLong(String key, Long initValue, Long seconds) {
        RedisAtomicLong result = new RedisAtomicLong(key, getStringRedisTemplate().getConnectionFactory(), initValue);
        result.expire(seconds, TimeUnit.SECONDS);
        return result;
    }

    /**
     * 自增
     */
    public static Long incr(String key) {
        return getStringRedisTemplate().opsForValue().increment(key);
    }
    /**
     * 自增
     */
    public static Long incr(String key, long delta) {
        return getStringRedisTemplate().opsForValue().increment(key, delta);
    }
    /**
     * 自减
     */
    public static void decr(String key) {
        getStringRedisTemplate().opsForValue().decrement(key);
    }


    /**
     * 自减
     */
    public static void decr(String key, long delta) {
        getStringRedisTemplate().opsForValue().decrement(key, delta);
    }

    /**
     * hash操作增加指定值
     */
    public static void hSet(String key, String hashKey, String value) {
        getStringRedisTemplate().opsForHash().put(key, hashKey, value);
    }

    /**
     * hash操作，value为对象
     */
    public static void hSetObject(String key, String hashKey, Object value) {
        getRedisTemplate().opsForHash().put(key, hashKey, value);
    }

    /**
     * hash操作增加Map
     */
    public static void hSetAll(String key, Map<?,?> map) {
        getRedisTemplate().opsForHash().putAll(key, map);
    }

    public static String hGet(String key, String hashKey){
        return (String)getStringRedisTemplate().opsForHash().get(key, hashKey);
    }
    /**
     * 获取hash一个hashkey对应的值
     */
    @SuppressWarnings("unchecked")
    public static<T> T hGetObject(String key, Object hashKey){
        return (T)getRedisTemplate().opsForHash().get(key, hashKey);
    }


    /**
     * hash操作删除
     */
    public static boolean hDel(String key, String hashKey) {
        return getStringRedisTemplate().opsForHash().delete(key, hashKey) > 0;
    }


    /**
     * 设置过期时间,单位为秒
     */
    public static void expire(String key, long seconds) {
        getStringRedisTemplate().expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 解锁
     */
    public static boolean unlock(String key) {
        return del(key);
    }
    /**
     * 判断缓存中是否有对应的key
     */
    public static boolean hasKey(final String key) {
        return Boolean.TRUE.equals(getStringRedisTemplate().hasKey(key));
    }

    /**
     * 拼接key
     */
    public static String getKey(String prefix, String... fields) {
        return String.format(prefix, fields);
    }

    /**
     * set操作， 添加
     */
    public static void addSet(String key, Object... values) {
        getRedisTemplate().opsForSet().add(key, values);
    }

    /**
     * set操作， 获取
     */
    public static<T> Set<T> getSetObject(String key) {
        return (Set<T>)getRedisTemplate().opsForSet().members(key);
    }

}
