package org.example.solid.ocp

class PremiumDiscount : DiscountPolicy {
    override fun calculate(total: Double) = total * 0.15
}