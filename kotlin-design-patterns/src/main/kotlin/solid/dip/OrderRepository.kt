package solid.dip

interface OrderRepository {
    fun save(orderId: String)
}