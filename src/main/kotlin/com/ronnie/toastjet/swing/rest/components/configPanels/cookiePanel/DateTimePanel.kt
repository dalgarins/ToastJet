package com.ronnie.toastjet.swing.rest.components.configPanels.cookiePanel

import java.awt.Dimension
import java.awt.GridLayout
import java.time.LocalDateTime
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class DateTimePanel {
    val panel = JPanel()
    private val yearSpinner = JSpinner(SpinnerNumberModel(2024, 1900, 2100, 1))
    private val monthSpinner = JSpinner(SpinnerNumberModel(1, 1, 12, 1))
    private val daySpinner = JSpinner(SpinnerNumberModel(1, 1, 31, 1))
    private val hourSpinner = JSpinner(SpinnerNumberModel(0, 0, 23, 1))
    private val minuteSpinner = JSpinner(SpinnerNumberModel(0, 0, 59, 1))
    private val secondSpinner = JSpinner(SpinnerNumberModel(0, 0, 59, 1))

    var dateTime: LocalDateTime = LocalDateTime.now()
        private set

    init {
        setupUI()
        updateDateTime()
        setupListeners()
    }

    private fun setupUI() {
        panel.layout = BoxLayout(panel, BoxLayout.LINE_AXIS)
        panel.add(JPanel().apply {
            preferredSize = Dimension(500, 60)
            maximumSize = Dimension(500, 60)
            layout = GridLayout(2, 6, 2, 2)

            add(JLabel("YYYY", JLabel.CENTER))
            add(JLabel("MM", JLabel.CENTER))
            add(JLabel("DD", JLabel.CENTER))
            add(JLabel("HH", JLabel.CENTER))
            add(JLabel("mm", JLabel.CENTER))
            add(JLabel("ss", JLabel.CENTER))

            add(yearSpinner)
            add(monthSpinner)
            add(daySpinner)
            add(hourSpinner)
            add(minuteSpinner)
            add(secondSpinner)
        })
    }

    private fun setupListeners() {
        val listener = ChangeListener { _: ChangeEvent? -> updateDateTime() }

        yearSpinner.addChangeListener(listener)
        monthSpinner.addChangeListener(listener)
        daySpinner.addChangeListener(listener)
        hourSpinner.addChangeListener(listener)
        minuteSpinner.addChangeListener(listener)
        secondSpinner.addChangeListener(listener)
    }

    private fun updateDateTime() {
        dateTime = LocalDateTime.of(
            (yearSpinner.value as Int),
            (monthSpinner.value as Int),
            (daySpinner.value as Int).coerceAtMost(getMaxDays()),
            (hourSpinner.value as Int),
            (minuteSpinner.value as Int),
            (secondSpinner.value as Int)
        )
    }

    private fun getMaxDays(): Int {
        val year = yearSpinner.value as Int
        val month = monthSpinner.value as Int
        return java.time.YearMonth.of(year, month).lengthOfMonth()
    }

    fun getJavaUtilDate(): java.util.Date {
        return java.util.Date.from(
            dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()
        )
    }
}