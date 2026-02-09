package com.foodchain.iotsolution.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateTimeUtils {
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    fun formatDateTime(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))
    fun formatTime(timestamp: Long): String = timeFormat.format(Date(timestamp))
    fun formatDate(timestamp: Long): String = dateFormat.format(Date(timestamp))
}

fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
        }
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "$hours ${if (hours == 1L) "hour" else "hours"} ago"
        }
        diff < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "$days ${if (days == 1L) "day" else "days"} ago"
        }
        diff < TimeUnit.DAYS.toMillis(30) -> {
            val weeks = TimeUnit.MILLISECONDS.toDays(diff) / 7
            "$weeks ${if (weeks == 1L) "week" else "weeks"} ago"
        }
        diff < TimeUnit.DAYS.toMillis(365) -> {
            val months = TimeUnit.MILLISECONDS.toDays(diff) / 30
            "$months ${if (months == 1L) "month" else "months"} ago"
        }
        else -> {
            val years = TimeUnit.MILLISECONDS.toDays(diff) / 365
            "$years ${if (years == 1L) "year" else "years"} ago"
        }
    }
}
