package com.ronnie.toastjet.utils.uiUtils

import com.intellij.util.IconUtil
import javax.swing.Icon

fun scaleIcon(icon: Icon, scaleFactor: Double): Icon {
    return IconUtil.scale(icon, null, scaleFactor.toFloat())
}