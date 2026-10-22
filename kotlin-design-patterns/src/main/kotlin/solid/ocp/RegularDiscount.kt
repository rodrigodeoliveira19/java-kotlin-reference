package org.example.solid.ocp

class RegularDiscount : DiscountPolicy {
    override fun calculate(total: Double) = total * 0.05
}