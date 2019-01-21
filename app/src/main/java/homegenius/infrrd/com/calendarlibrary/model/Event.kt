package homegenius.infrrd.com.calendarlibrary.model

data class Event(
    var id: Long?,
    var startTS: Long = 0L,
    var endTS: Long = 0L,
    var title: String = "",
    var location: String = "",
    var description: String = "",
    var reminder1Minutes: Int = -1,
    var reminder2Minutes: Int = -1,
    var reminder3Minutes: Int = -1,
    var repeatInterval: Int = 0,
    var repeatRule: Int = 0,
    var repeatLimit: Long = 0L,
    var repetitionExceptions: ArrayList<String> = ArrayList(),
    var importId: String = "",
    var flags: Int = 0,
    var parentId: Long = 0,
    var lastUpdated: Long = 0L)
