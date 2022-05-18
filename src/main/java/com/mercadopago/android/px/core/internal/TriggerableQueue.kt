package com.mercadopago.android.px.core.internal

import java.util.*

typealias EventQueue = () -> Unit

class TriggerableQueue {

    private val queue = LinkedList<EventQueue>()
    private var triggered = false

    fun enqueue(event: EventQueue) {
        if (triggered) {
            event.invoke()
        } else {
            queue.add(event)
        }
    }

    fun execute() {
        while (queue.isNotEmpty()) { queue.poll().invoke() }
        triggered = true
    }
}