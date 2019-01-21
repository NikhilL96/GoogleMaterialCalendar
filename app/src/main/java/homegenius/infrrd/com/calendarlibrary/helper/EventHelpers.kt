package homegenius.infrrd.com.calendarlibrary.helper

import homegenius.infrrd.com.calendarlibrary.extensions.seconds
import homegenius.infrrd.com.calendarlibrary.model.Event
import org.joda.time.DateTime
import kotlin.random.Random

object EventHelpers {

    fun getEvents(startTS: Long, endTS: Long, callBack: (events: ArrayList<Event>) -> Unit) {
        val events: ArrayList<Event> = ArrayList()
        var startDate = DateTime()
        var endDate = DateTime()
        startDate = Formatter.getDateTimeFromCode("20190112")
        endDate = Formatter.getDateTimeFromCode("20190114")

        events.add(
            Event(
                1,
                startDate.withSecondOfMinute(0).withMillisOfSecond(0).seconds(),
                endDate.withSecondOfMinute(0).withMillisOfSecond(0).seconds(),
                "Event 1"
            )
        )
//
        startDate = Formatter.getDateTimeFromCode("20190107")
        endDate = Formatter.getDateTimeFromCode("20190114")
        events.add(
            Event(
                2,
                startDate.withSecondOfMinute(0).withMillisOfSecond(0).seconds(),
                endDate.withSecondOfMinute(0).withMillisOfSecond(0).seconds(),
                "Event 2"
            )
        )

        startDate = Formatter.getDateTimeFromCode("20190122")
        endDate = Formatter.getDateTimeFromCode("20190207")
        events.add(
            Event(
                3,
                startDate.withSecondOfMinute(0).withMillisOfSecond(0).seconds(),
                endDate.withSecondOfMinute(0).withMillisOfSecond(0).seconds(),
                "Event 3"
            )
        )
        callBack.invoke(events)
    }
}