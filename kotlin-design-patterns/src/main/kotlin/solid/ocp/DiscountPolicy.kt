package org.example.solid.ocp

interface DiscountPolicy {
    fun calculate(total: Double): Double
}