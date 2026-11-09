package com.demo.appfinanceiro.adapter.output.cache.redis

import com.demo.appfinanceiro.application.port.output.IdempotencyRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisIdempotencyAdapter(
    private val redisTemplate: StringRedisTemplate
) : IdempotencyRepository {

    companion object {
        private const val KEY_PREFIX = "idempotency:"
        private val TTL = Duration.ofSeconds(360)
    }

    override fun tryAcquire(key: String, paymentId: String): Boolean {
        return redisTemplate.opsForValue()
            .setIfAbsent("$KEY_PREFIX$key", paymentId, TTL) ?: false
    }

    override fun getPaymentId(key: String): String? {
        return redisTemplate.opsForValue().get("$KEY_PREFIX$key")
    }
}
