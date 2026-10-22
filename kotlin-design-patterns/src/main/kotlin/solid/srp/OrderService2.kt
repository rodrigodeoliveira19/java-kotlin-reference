package org.example.solid.srp

import org.example.solid.srp.model.Order

/*
 S — Single Responsibility Principle
 Responsabilidade única

Uma classe deve ter um único motivo para mudar.

Agora cada classe tem um papel mais claro. Se a implementação do e-mail mudar, não precisamos alterar a classe responsável pela persistência.
Atenção: SRP não significa que toda classe precisa ter apenas um método. Significa que seus comportamentos devem estar relacionados a uma responsabilidade coesa.

*/
class OrderService2(
    private val calculator: OrderCalculator,
    private val repository: OrderRepository,
    private val emailSender: EmailSender
) {
    fun checkout(order: Order) {
        calculator.calculate(order)
        repository.save(order)
        emailSender.sendConfirmation(order)
    }
}