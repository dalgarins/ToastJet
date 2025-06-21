package com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.enums.HttpMethod
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.responseRequestBody.ConstructResReqBody
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.responseRequestBody.ResponseRequestBodyPanel
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.widgets.RadioTabbedPanel
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class ResponseRequestPanel(val store: RequestStore) : JPanel() {


    val methodPanel = JLabel().apply {
        border = JBUI.Borders.empty(4, 10, 5, 5)
        font = JBFont.h3()
    }

    val httpUrlPanel = JLabel().apply {
        border = JBUI.Borders.empty(4, 5, 5, 0)
        font = JBFont.h3()
    }

    var urlPanel: JPanel = JPanel().apply {
        layout = FlowLayout(FlowLayout.LEFT)
        add(methodPanel)
        add(httpUrlPanel)
    }


    var radioTabbedPanel = RadioTabbedPanel(
        store.theme,
        tabs = mutableListOf()
    )

    private fun addEmptyMessage(tabName: String): JComponent {
        val panel = JPanel(BorderLayout()).apply {
            background = store.theme.getState().globalScheme.defaultBackground
            val label = JBLabel("No $tabName").apply {
                font = JBFont.h2()
                foreground = store.theme.getState().globalScheme.defaultForeground
                horizontalAlignment = SwingConstants.CENTER
            }
            add(label, BorderLayout.CENTER)
        }
        return panel
    }

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        populatePanel()
        add(urlPanel)
        add(radioTabbedPanel)

        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
    }

    fun setTheme(theme: EditorColorsManager) {
        foreground = theme.globalScheme.defaultForeground
        background = theme.globalScheme.defaultBackground
        urlPanel.background = background
        urlPanel.foreground = foreground
        radioTabbedPanel.background = background
        radioTabbedPanel.foreground = foreground
    }


    fun populatePanel() {
        val sendRequest = store.response.getState().apiRequestData

        methodPanel.text = sendRequest.method.name.uppercase() + " : "
        methodPanel.foreground = when (sendRequest.method) {
            HttpMethod.GET -> JBColor.GREEN
            HttpMethod.POST -> JBColor.YELLOW
            HttpMethod.PUT -> JBColor.ORANGE
            HttpMethod.PATCH -> JBColor.BLUE
            HttpMethod.DELETE -> JBColor.RED
            HttpMethod.HEAD -> JBColor.CYAN
            else -> JBColor.BLACK
        }
        httpUrlPanel.text = sendRequest.url
        val headersData = HashMap<String, String>()
        sendRequest.headers.forEach {
            if (it.isChecked) {
                headersData[it.key] = it.value
            }
        }
        if (headersData.isEmpty()) {
            radioTabbedPanel.addTab("Headers", addEmptyMessage("Headers"))

        } else {
            radioTabbedPanel.addTab("Headers", ConstructResReqBody(headersData, store.theme))
        }

        val paramsData = HashMap<String, String>()
        sendRequest.params.forEach {
            if (it.isChecked) {
                headersData[it.key] = it.value
            }
        }
        if (paramsData.isEmpty()) {
            radioTabbedPanel.addTab("Params", addEmptyMessage("Params"))
        } else {
            radioTabbedPanel.addTab("Params", ConstructResReqBody(headersData, store.theme))
        }

        val pathData = HashMap<String, String>()
        sendRequest.path.forEach {
            headersData[it.key] = it.value
        }
        if (pathData.isEmpty()) {
            radioTabbedPanel.addTab("Paths", addEmptyMessage("Paths"))
        } else {
            radioTabbedPanel.addTab("Headers", ConstructResReqBody(headersData, store.theme))
        }
        radioTabbedPanel.addTab("Body", ResponseRequestBodyPanel(store))
    }

}