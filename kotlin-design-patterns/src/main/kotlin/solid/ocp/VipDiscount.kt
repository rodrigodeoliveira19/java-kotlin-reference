package org.example.solid.ocp

class VipDiscount : DiscountPolicy {
    override fun calculate(total: Double) = total * 0.10
}