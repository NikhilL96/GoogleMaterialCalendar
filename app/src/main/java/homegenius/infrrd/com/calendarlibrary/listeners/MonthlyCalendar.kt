package homegenius.infrrd.com.calendarlibrary.listeners

import android.content.Context
import homegenius.infrrd.com.calendarlibrary.model.DayMonthly
import org.joda.time.DateTime

interface MonthlyCalendar {
    fun updateMonthlyCalendar(context: Context, month: String, days: ArrayList<DayMonthly>, checkedEvents: Boolean, currTargetDate: DateTime)
}