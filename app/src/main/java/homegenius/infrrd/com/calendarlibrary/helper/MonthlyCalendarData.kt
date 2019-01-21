package homegenius.infrrd.com.calendarlibrary.helper

import android.content.Context
import homegenius.infrrd.com.calendarlibrary.extensions.seconds
import homegenius.infrrd.com.calendarlibrary.helper.EventHelpers.getEvents
import homegenius.infrrd.com.calendarlibrary.listeners.MonthlyCalendar
import homegenius.infrrd.com.calendarlibrary.model.DayMonthly
import homegenius.infrrd.com.calendarlibrary.model.Event
import org.joda.time.DateTime
import java.util.HashMap

class MonthlyCalendarData(val callback: MonthlyCalendar, val context: Context) {
    private val DAYS_CNT = 42
    private val YEAR_PATTERN = "YYYY"

    private val today: String = DateTime().toString(Formatter.DAYCODE_PATTERN)
    private var events = ArrayList<Event>()

    lateinit var targetDate: DateTime

    fun updateMonthlyCalendar(targetDate: DateTime) {
        this.targetDate = targetDate
        val startTS = this.targetDate.minusDays(7).seconds()
        val endTS = this.targetDate.plusDays(43).seconds()
        getEvents(startTS, endTS) {
            gotEvents(it)
        }
    }


    fun getDays(markDaysWithEvents: Boolean) {
        val days = ArrayList<DayMonthly>(DAYS_CNT)
        val currMonthDays = targetDate.dayOfMonth().maximumValue
        val firstDayIndex = targetDate.withDayOfMonth(1).dayOfWeek

        val prevMonthDays = targetDate.minusMonths(1).dayOfMonth().maximumValue

        var isThisMonth = false
        var isToday: Boolean
        var value = prevMonthDays - firstDayIndex + 1
        var curDay = targetDate

        for (i in 0 until DAYS_CNT) {
            when {
                i < firstDayIndex -> {
                    isThisMonth = false
                    curDay = targetDate.withDayOfMonth(1).minusMonths(1)
                }
                i == firstDayIndex -> {
                    value = 1
                    isThisMonth = true
                    curDay = targetDate
                }
                value == currMonthDays + 1 -> {
                    value = 1
                    isThisMonth = false
                    curDay = targetDate.withDayOfMonth(1).plusMonths(1)
                }
            }

            isToday = isToday(curDay, value)

            val newDay = curDay.withDayOfMonth(value)
            val dayCode = Formatter.getDayCodeFromDateTime(newDay)
            val day = DayMonthly(value, isThisMonth, isToday, dayCode, newDay.weekOfWeekyear, ArrayList(), i)
            days.add(day)
            value++
        }

        if (markDaysWithEvents) {
            markDaysWithEvents(days)
        } else {
            callback.updateMonthlyCalendar(context, monthName, days, false, targetDate)
        }
    }

    // it works more often than not, dont touch
    private fun markDaysWithEvents(days: ArrayList<DayMonthly>) {
        val dayEvents = HashMap<String, ArrayList<Event>>()
        events.forEach {
            val startDateTime = Formatter.getDateTimeFromTS(it.startTS)
            val endDateTime = Formatter.getDateTimeFromTS(it.endTS)
            val endCode = Formatter.getDayCodeFromDateTime(endDateTime)

            var currDay = startDateTime
            var dayCode = Formatter.getDayCodeFromDateTime(currDay)
            var currDayEvents = dayEvents[dayCode] ?: ArrayList()
            currDayEvents.add(it)
            dayEvents[dayCode] = currDayEvents

            while (Formatter.getDayCodeFromDateTime(currDay) != endCode) {
                currDay = currDay.plusDays(1)
                dayCode = Formatter.getDayCodeFromDateTime(currDay)
                currDayEvents = dayEvents[dayCode] ?: ArrayList()
                currDayEvents.add(it)
                dayEvents[dayCode] = currDayEvents
            }
        }

        days.filter { dayEvents.keys.contains(it.code) }.forEach {
            it.dayEvents = dayEvents[it.code]!!
        }
        callback.updateMonthlyCalendar(context, monthName, days, true, targetDate)
    }

    private fun isToday(targetDate: DateTime, curDayInMonth: Int): Boolean {
        val targetMonthDays = targetDate.dayOfMonth().maximumValue
        return targetDate.withDayOfMonth(Math.min(curDayInMonth, targetMonthDays)).toString(Formatter.DAYCODE_PATTERN) == today
    }

    private val monthName: String
        get() {
            var month = Formatter.getMonthName(context, targetDate.monthOfYear)
            val targetYear = targetDate.toString(YEAR_PATTERN)
            if (targetYear != DateTime().toString(YEAR_PATTERN)) {
                month += " $targetYear"
            }
            return month
        }

    private fun gotEvents(events: ArrayList<Event>) {
        this.events = events
        getDays(true)
    }
}