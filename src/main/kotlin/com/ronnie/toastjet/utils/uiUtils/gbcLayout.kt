package com.ronnie.toastjet.utils.uiUtils

import java.awt.GridBagConstraints

fun gbcLayout(
    gbc: GridBagConstraints,
    x: Int? = null,
    y: Int? = null,
    weightX: Double? = null,
    weightY: Double? = null
): GridBagConstraints {
    if (x != null) gbc.gridx = x
    if (y != null) gbc.gridy = y
    if (weightX != null) gbc.weightx = weightX
    if (weightY != null) gbc.weighty = weightY
    return gbc
}