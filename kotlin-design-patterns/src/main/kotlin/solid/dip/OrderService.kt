package solid.dip

/**
 * O serviço cria sua própria dependência. Se quisermos trocar o MySQL por PostgreSQL, usar um repositório em memória nos testes ou adotar outra solução de persistência, precisaremos mexer no acoplamento entre as classes.
 *
 * A solução: depender de uma abstração
 * Primeiro, definimos o contrato:
 *
 *
 * kotlin
 * interface OrderRepository {
 *     fun save(orderId: String)
 * }
 */
class OrderService {
    private val repository = MySqlOrderRepository()

    fun createOrder(orderId: String) {
        repository.save(orderId)
    }
}