package solid.dip

class MySqlOrderRepository2 : OrderRepository {
    override fun save(orderId: String) {
        println("Salvando $orderId no MySQL")
    }
}