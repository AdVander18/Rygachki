package com.example.rygachki.data

import android.content.Context
import java.time.LocalDate
import java.time.YearMonth

class CounterStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getCount(date: LocalDate): Int = prefs.getInt(key(date), 0)

    fun getMonthCount(yearMonth: YearMonth): Int =
        (1..yearMonth.lengthOfMonth()).sumOf { getCount(yearMonth.atDay(it)) }

    var todayCount: Int
        get() = getCount(LocalDate.now())
        set(value) = prefs.edit().putInt(key(LocalDate.now()), value).apply()

    private fun key(date: LocalDate) = "$PREFIX${date}"

    private companion object {
        const val PREFS_NAME = "rygachki_prefs"
        const val PREFIX = "d_"
    }
}
