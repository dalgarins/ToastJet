package com.ronnie.toastjet.swing.store

class StateHolder<T>(initialValue: T) {
    private var state : T

    init {
        state = initialValue
    }

    private val onStateChange = mutableListOf<(T) -> Unit>()

    fun getState(): T = state

    fun setState(value: T) {
        state = value
        println("${onStateChange.size}")
        onStateChange.forEach {
            it(value)
        }
    }
    fun setState(stateFunction:(currentState:T)->T){
        state = stateFunction(state)
        onStateChange.forEach { it(state) }

    }

    fun addListener(listener: (T) -> Unit) {
        onStateChange.add(listener)
    }

    fun removeListener(listener: (T) -> Unit) {
        onStateChange.remove(listener)
    }
}

