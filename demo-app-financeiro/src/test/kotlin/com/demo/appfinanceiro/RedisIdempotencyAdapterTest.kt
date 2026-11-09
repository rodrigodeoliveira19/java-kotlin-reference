package com.demo.appfinanceiro

import com.demo.appfinanceiro.adapter.output.cache.redis.RedisIdempotencyAdapter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class RedisIdempotencyAdapterTest {

    @Test
    fun `should acquire key when it does not exist`() {
        val redisTemplate = mock(StringRedisTemplate::class.java)
        val valueOperations = mock(ValueOperations::class.java) as ValueOperations<String, String>

        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)
        `when`(valueOperations.setIfAbsent("idempotency:test-key-1", "PAYMENT-1", Duration.ofHours(24)))
            .thenReturn(true)
        `when`(valueOperations.get("idempotency:test-key-1")).thenReturn("PAYMENT-1")

        val adapter = RedisIdempotencyAdapter(redisTemplate)

        val acquired = adapter.tryAcquire("test-key-1", "PAYMENT-1")

        assertTrue(acquired)
        assertEquals("PAYMENT-1", adapter.getPaymentId("test-key-1"))
    }

    @Test
    fun `should reject key already existing`() {
        val redisTemplate = mock(StringRedisTemplate::class.java)
        val valueOperations = mock(ValueOperations::class.java) as ValueOperations<String, String>

        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)
        `when`(valueOperations.setIfAbsent("idempotency:test-key-2", "PAYMENT-3", Duration.ofHours(24)))
            .thenReturn(false)

        val adapter = RedisIdempotencyAdapter(redisTemplate)

        val acquired = adapter.tryAcquire("test-key-2", "PAYMENT-3")

        assertFalse(acquired)
    }

    @Test
    fun `should return payment id associated with key`() {
        val redisTemplate = mock(StringRedisTemplate::class.java)
        val valueOperations = mock(ValueOperations::class.java) as ValueOperations<String, String>

        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)
        `when`(valueOperations.get("idempotency:test-key-3")).thenReturn("PAYMENT-99")

        val adapter = RedisIdempotencyAdapter(redisTemplate)

        assertEquals("PAYMENT-99", adapter.getPaymentId("test-key-3"))
    }
}
