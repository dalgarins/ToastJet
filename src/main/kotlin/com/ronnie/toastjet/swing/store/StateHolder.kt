package com.ronnie.toastjet.swing.store

class StateHolder<T>(initialValue: T) {
    private var state: T

    init {
        state = initialValue
    }

    private val onStateChange = mutableListOf<(T) -> Unit>()
    private val effectChange = mutableListOf<(T) -> Unit>()

    fun getState(): T = state

    fun setState(value: T) {
        state = value
        println("${onStateChange.size}")
        onStateChange.forEach {
            it(value)
        }
    }

    fun setState(stateFunction: (currentState: T) -> T) {
        state = stateFunction(state)
        effectChange.forEach { it(state) }
        onStateChange.forEach { it(state) }

    }

    fun addListener(listener: (T) -> Unit) {
        onStateChange.add(listener)
    }

    fun addEffect(listener: (T) -> Unit) {
        effectChange.add(listener)
    }

    fun removeEffect(listener: (T) -> Unit) {
        effectChange.remove(listener)
    }

    fun removeListener(listener: (T) -> Unit) {
        onStateChange.remove(listener)
    }
}

