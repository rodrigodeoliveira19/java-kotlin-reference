package solid.dip

/**
 * A classe OrderService2 depende de uma abstração (OrderRepository) em vez de uma implementação concreta.
 * Isso permite que diferentes implementações de OrderRepository sejam injetadas, promovendo flexibilidade e testabilidade.
 *
 * Podemos escolher a implementação sem alterar o serviço:
 *
 *
 * kotlin
 * val productionService =
 *     OrderService(MySqlOrderRepository())
 *
 * val testService =
 *     OrderService(InMemoryOrderRepository())
 */
class OrderService2(
    private val repository: OrderRepository
) {
    fun createOrder(orderId: String) {
        repository.save(orderId)
    }
}