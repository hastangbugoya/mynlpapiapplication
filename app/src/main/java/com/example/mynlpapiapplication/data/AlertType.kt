package com.example.mynlpapiapplication.data

import com.example.mynlpapiapplication.R

/**
 * Alert types and snackbar colors
 * @param bgColor resource id for background color
 * @param fgColor resource id for text color
 */
enum class AlertType(val bgColor: Int, val fgColor: Int) {
    DEFAULT(R.color.alert_color_notify, R.color.white),
    ERROR(R.color.alert_color_error, R.color.white)
}