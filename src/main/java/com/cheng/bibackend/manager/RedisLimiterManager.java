package com.cheng.bibackend.manager;

import com.cheng.bibackend.common.ErrorCode;
import com.cheng.bibackend.exception.ThrowUtils;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     */
    public void doRateLimit(String key){
        //创建限流器 每个限流器是分别统计的
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //定义限流的 规格  4个参数
        // 每秒最多访问 2 次
        // 参数1 type：限流类型，可以是自定义的任何类型，用于区分不同的限流策略。
        // 参数2 rate：限流速率，即单位时间内允许通过的请求数量。
        // 参数3 rateInterval：限流时间间隔，即限流速率的计算周期长度。
        // 参数4 unit：限流时间间隔单位，可以是秒、毫秒等。
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌   tryAcquire取一个令牌 写1就行 如果你希望每个操作占用2令牌就写2
        boolean canOp = rateLimiter.tryAcquire(1);
        ThrowUtils.throwIf(!canOp, ErrorCode.TOO_MANY_REQUEST);

    }
}
