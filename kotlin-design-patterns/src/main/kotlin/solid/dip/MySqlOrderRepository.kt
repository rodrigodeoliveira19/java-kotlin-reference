package solid.dip

class MySqlOrderRepository {
    fun save(orderId: String) {
        println("Salvando $orderId no MySQL")
    }
}