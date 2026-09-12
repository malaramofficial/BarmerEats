package com.example

import com.example.data.OrderActor
import com.example.data.OrderStateMachine
import com.example.data.OrderStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OrderStateMachineTest {
    @Test
    fun restaurantCanAdvanceOrderThroughPreparation() {
        assertTrue(OrderStateMachine.canTransition(OrderStatus.PLACED, OrderStatus.ACCEPTED, OrderActor.RESTAURANT))
        assertTrue(OrderStateMachine.canTransition(OrderStatus.ACCEPTED, OrderStatus.PREPARING, OrderActor.RESTAURANT))
        assertTrue(OrderStateMachine.canTransition(OrderStatus.PREPARING, OrderStatus.READY, OrderActor.RESTAURANT))
    }

    @Test
    fun restaurantCannotMarkOrderDelivered() {
        assertFalse(OrderStateMachine.canTransition(OrderStatus.READY, OrderStatus.DELIVERED, OrderActor.RESTAURANT))
    }

    @Test
    fun customerCanOnlyCancelPlacedOrder() {
        assertTrue(OrderStateMachine.canTransition(OrderStatus.PLACED, OrderStatus.CANCELLED, OrderActor.CUSTOMER))
        assertFalse(OrderStateMachine.canTransition(OrderStatus.ACCEPTED, OrderStatus.CANCELLED, OrderActor.CUSTOMER))
    }

    @Test
    fun riderCannotSkipDeliveryStates() {
        assertTrue(OrderStateMachine.canTransition(OrderStatus.RIDER_ASSIGNED, OrderStatus.PICKED_UP, OrderActor.RIDER))
        assertFalse(OrderStateMachine.canTransition(OrderStatus.READY, OrderStatus.DELIVERED, OrderActor.RIDER))
    }
}
