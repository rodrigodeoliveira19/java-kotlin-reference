package solid.dip

class InMemoryOrderRepository : OrderRepository {
    private val orders = mutableListOf<String>()

    override fun save(orderId: String) {
        orders.add(orderId)
    }
}