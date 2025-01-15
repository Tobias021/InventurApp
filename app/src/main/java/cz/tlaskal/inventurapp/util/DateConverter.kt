package cz.tlaskal.inventurapp.util

import java.util.Date

fun dateToTimestamp(date: Date): Long{
    return date.time
}

fun timestampToDate(timestamp: Long): Date{
    return Date(timestamp, )
}