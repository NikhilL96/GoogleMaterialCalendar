package homegenius.infrrd.com.calendarlibrary.model

class DayMonthly(val value: Int, val isThisMonth: Boolean, val isToday: Boolean, val code: String, val weekOfYear: Int, var dayEvents: ArrayList<Event>,
                      var indexOnMonthView: Int)
