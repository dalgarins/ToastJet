package com.ronnie.toastjet.swing.socket.request

import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.SocketStore
import javax.swing.JPanel

class SocketDataComponent(
    val store: SocketStore,
    val configStore: ConfigStore
) : JPanel() {
}