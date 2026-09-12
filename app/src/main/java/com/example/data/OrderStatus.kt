package com.example.data

enum class OrderStatus {
    PLACED,
    ACCEPTED,
    REJECTED,
    PREPARING,
    READY,
    RIDER_ASSIGNED,
    PICKED_UP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    REFUND_PENDING,
    REFUNDED;

    companion object {
        fun parse(value: String): OrderStatus? = entries.firstOrNull { it.name == value.uppercase() }
    }
}

enum class OrderActor {
    CUSTOMER,
    RESTAURANT,
    RIDER,
    ADMIN
}

object OrderStateMachine {
    fun canTransition(from: OrderStatus, to: OrderStatus, actor: OrderActor): Boolean = when (actor) {
        OrderActor.CUSTOMER -> from == OrderStatus.PLACED && to == OrderStatus.CANCELLED
        OrderActor.RESTAURANT -> when (from) {
            OrderStatus.PLACED -> to == OrderStatus.ACCEPTED || to == OrderStatus.REJECTED
            OrderStatus.ACCEPTED -> to == OrderStatus.PREPARING
            OrderStatus.PREPARING -> to == OrderStatus.READY
            else -> false
        }
        OrderActor.RIDER -> when (from) {
            OrderStatus.READY -> to == OrderStatus.RIDER_ASSIGNED
            OrderStatus.RIDER_ASSIGNED -> to == OrderStatus.PICKED_UP
            OrderStatus.PICKED_UP -> to == OrderStatus.OUT_FOR_DELIVERY
            OrderStatus.OUT_FOR_DELIVERY -> to == OrderStatus.DELIVERED
            else -> false
        }
        OrderActor.ADMIN -> true
    }
}
