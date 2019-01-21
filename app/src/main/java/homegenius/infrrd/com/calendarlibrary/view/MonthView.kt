package homegenius.infrrd.com.calendarlibrary.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.SparseIntArray
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.Toast
import homegenius.infrrd.com.calendarlibrary.R
import homegenius.infrrd.com.calendarlibrary.extensions.moveLastItemToFront
import homegenius.infrrd.com.calendarlibrary.extensions.seconds
import homegenius.infrrd.com.calendarlibrary.fragments.GridAdapter
import homegenius.infrrd.com.calendarlibrary.helper.Formatter
import homegenius.infrrd.com.calendarlibrary.model.DayMonthly
import homegenius.infrrd.com.calendarlibrary.model.Event
import homegenius.infrrd.com.calendarlibrary.model.MonthViewEvent
import kotlinx.android.synthetic.main.month_view_layout.*
import kotlinx.android.synthetic.main.month_view_layout.view.*
import org.joda.time.DateTime
import org.joda.time.Days
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random

class MonthView(context: Context, attrs: AttributeSet, defStyle: Int) : View(context, attrs, defStyle) {

    private var paintEnabled: Paint = Paint()
    private var paintDisabled: Paint = Paint()
    private var currentDateCirclePaint = Paint()
    private val BG_CORNER_RADIUS = 4f
    private val ROW_COUNT = 6
    private val COLUMN_COUNT = 7

    private var dayWidth = 0f
    private var dayHeight = 0f
    private var textSize = 0
    private var weekDaysLetterHeight = 0
    private var eventTitleHeight = 0
    private var currDayOfWeek = 0
    private var smallPadding = 5
    private var currentDayEventPadding = 30
    private var dateTextTopPadding = 20
    private var maxEventsPerDay = 0
    private var horizontalOffset = 0
    private var showWeekNumbers = false
    private val colorArray = arrayListOf<Int>()
    private var dimPastEvents = true
    private var bgRectF = RectF()
    private lateinit var dayLetters: ArrayList<String>

    private var dayVerticalOffsets = SparseIntArray()
    private var days = ArrayList<DayMonthly>()
    private var weekDayLetterPaint = Paint()
    private var eventTitlePaint = TextPaint()

    private lateinit var dateSelectedCallback: ((DayMonthly) -> Unit)

    private var allEventArray = arrayListOf<MonthViewEvent>()

    private lateinit var gridView:View


    init {
        textSize = resources.getDimensionPixelSize(R.dimen.date_text_size)
        
        paintEnabled.color = ContextCompat.getColor(context, R.color.primary_text)
        paintEnabled.textSize = textSize.toFloat()
        paintDisabled.textSize = textSize.toFloat()
        paintDisabled.color = ContextCompat.getColor(context, R.color.brown_grey)
        paintDisabled.textAlign = Paint.Align.CENTER
        paintEnabled.textAlign = Paint.Align.CENTER
        paintDisabled.isSubpixelText = true
        paintDisabled.isAntiAlias = true
        eventTitlePaint.isSubpixelText = true
        eventTitlePaint.isAntiAlias = true
        weekDayLetterPaint.color = ContextCompat.getColor(context, R.color.brown_grey)
        weekDayLetterPaint.textSize = 30f
        weekDayLetterPaint.textAlign = Paint.Align.CENTER
        weekDaysLetterHeight = textSize * 2
        eventTitleHeight = textSize
        eventTitlePaint.color = ContextCompat.getColor(context, R.color.white)
        eventTitlePaint.textSize = textSize.toFloat()
        eventTitlePaint.isSubpixelText = true
        eventTitlePaint.isAntiAlias = true

        colorArray.add(ContextCompat.getColor(context, R.color.red))
        colorArray.add(ContextCompat.getColor(context, R.color.blue))
        colorArray.add(ContextCompat.getColor(context, R.color.green))

        currentDateCirclePaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        initWeekDayLetters()
    }

    fun assignGridView(view:View) {
        gridView = view
    }

    fun updateDays(newDays: ArrayList<DayMonthly>, dateSelectedCallback: ((DayMonthly) -> Unit)) {
        days = newDays
        this.dateSelectedCallback = dateSelectedCallback

        horizontalOffset = if (showWeekNumbers) eventTitleHeight * 2 else 0
        initWeekDayLetters()
        groupAllEvents()
        invalidate()
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        dayVerticalOffsets.clear()
        canvas?.let {
            measureDaySize(it)
            addWeekDayLetters(it)
            drawGrid(it)
            initGrid(gridView)

        }


        var curId = 0
        for (y in 0..ROW_COUNT) {
            for (x in 0 until COLUMN_COUNT) {
                val day = days.getOrNull(curId)
                if (day != null) {
                    dayVerticalOffsets.put(
                        day.indexOnMonthView,
                        dayVerticalOffsets[day.indexOnMonthView] + weekDaysLetterHeight + dateTextTopPadding
                    )
                    val verticalOffset = dayVerticalOffsets[day.indexOnMonthView]
                    val xPos = x * dayWidth + horizontalOffset
                    val yPos = y * dayHeight + verticalOffset
                    val xPosCenter = xPos + dayWidth / 2
                    if (day.isToday) {
                        canvas?.drawCircle(
                            xPosCenter,
                            yPos + paintEnabled.textSize * 0.7f,
                            paintEnabled.textSize * 0.8f,
                            currentDateCirclePaint
                        )
                    }
                    canvas?.drawText(day.value.toString(), xPosCenter, yPos + textSize, getTextPaint(day))
                    dayVerticalOffsets.put(day.indexOnMonthView, (verticalOffset + textSize * 2))
                }
                curId++
            }
        }

        for (event in allEventArray) {
            drawEvent(event, canvas!!)
        }
    }

    private fun drawGrid(canvas: Canvas) {
        // vertical lines
        val gridPaint = Paint()
        gridPaint.color = ContextCompat.getColor(context, R.color.brown_grey)
        for (i in 0 until COLUMN_COUNT) {
            var lineX = i * dayWidth
            if (showWeekNumbers) {
                lineX += horizontalOffset
            }
            canvas.drawLine(lineX, 0f, lineX, canvas.height.toFloat(), gridPaint)
        }

        // horizontal lines
        canvas.drawLine(0f, 0f, canvas.width.toFloat(), 0f, gridPaint)
        for (i in 0..(ROW_COUNT - 1)) {
            canvas.drawLine(
                0f,
                i * dayHeight + weekDaysLetterHeight,
                canvas.width.toFloat(),
                i * dayHeight + weekDaysLetterHeight,
                gridPaint
            )
        }
    }

    private fun groupAllEvents() {
        days.forEach {
            val day = it
            day.dayEvents.forEach {
                val event = it

                // make sure we properly handle events lasting multiple days and repeating ones
                val lastEvent = allEventArray.lastOrNull { it.id == event.id }
                val daysCnt = getEventLastingDaysCount(event)
                if (lastEvent == null || lastEvent.startDayIndex + daysCnt <= day.indexOnMonthView) {
                    val monthViewEvent = MonthViewEvent(
                        event.id!!, event.title, event.startTS, colorArray[(0 until 3).random()], day.indexOnMonthView,
                        daysCnt, day.indexOnMonthView
                    )
                    allEventArray.add(monthViewEvent)
                }
            }
        }

        allEventArray = allEventArray.asSequence().sortedWith(
            compareBy({ -it.daysCnt },
                { it.startTS },
                { it.startDayIndex },
                { it.title })
        )
            .toMutableList() as ArrayList<MonthViewEvent>
    }

    private fun getTextPaint(startDay: DayMonthly): Paint {
        var paintColor = paintEnabled
        if (startDay.isToday) {
            paintColor.color = ContextCompat.getColor(context, R.color.white)
        } else {
            paintColor.color = ContextCompat.getColor(context, R.color.primary_text)
        }

        if (!startDay.isThisMonth) {
            paintColor = paintDisabled
        }

        return paintColor
    }

    private fun initWeekDayLetters() {
        dayLetters = context.resources.getStringArray(R.array.week_day_letters).toMutableList() as ArrayList<String>
    }

    private fun addWeekDayLetters(canvas: Canvas) {
        for (i in 0 until COLUMN_COUNT) {
            val xPos = horizontalOffset + (i + 1) * dayWidth - dayWidth / 2
            canvas.drawText(dayLetters[i], xPos, weekDaysLetterHeight * 0.7f, weekDayLetterPaint)
        }
    }

    private fun measureDaySize(canvas: Canvas) {
        dayWidth = (canvas.width - horizontalOffset) / (COLUMN_COUNT).toFloat()
        dayHeight = (canvas.height - weekDaysLetterHeight) / ROW_COUNT.toFloat()
        val availableHeightForEvents = dayHeight.toInt() - weekDaysLetterHeight
        maxEventsPerDay = availableHeightForEvents / eventTitleHeight
    }

    private fun drawEvent(event: MonthViewEvent, canvas: Canvas) {
        val verticalOffset = dayVerticalOffsets[event.startDayIndex]
        val xPos = event.startDayIndex % 7 * dayWidth + horizontalOffset
        val yPos = (event.startDayIndex / 7) * dayHeight
        val xPosCenter = xPos + dayWidth / 2

        if (verticalOffset - eventTitleHeight * 2 > dayHeight) {
            canvas.drawText(
                "...",
                xPosCenter,
                yPos + verticalOffset - eventTitleHeight / 2,
                getTextPaint(days[event.startDayIndex])
            )
            return
        }

        // event background rectangle
        val backgroundY = yPos + verticalOffset  + currentDayEventPadding
        val bgLeft = xPos + smallPadding
        val bgTop = backgroundY + smallPadding - eventTitleHeight
        var bgRight = xPos - smallPadding + dayWidth * event.daysCnt
        val bgBottom = backgroundY + smallPadding * 2
        if (bgRight > canvas.width.toFloat()) {
            bgRight = canvas.width.toFloat() - smallPadding
            val newStartDayIndex = (event.startDayIndex / 7 + 1) * 7
            if (newStartDayIndex < 42) {
                val newEvent = event.copy(
                    startDayIndex = newStartDayIndex,
                    daysCnt = event.daysCnt - (newStartDayIndex - event.startDayIndex)
                )
                drawEvent(newEvent, canvas)
            }
        }

        val startDayIndex = days[event.originalStartDayIndex]
        val endDayIndex = days[Math.min(event.startDayIndex + event.daysCnt - 1, 41)]
        bgRectF.set(bgLeft, bgTop, bgRight, bgBottom)
        canvas.drawRoundRect(
            bgRectF,
            BG_CORNER_RADIUS,
            BG_CORNER_RADIUS,
            getEventBackgroundColor(event, startDayIndex, endDayIndex)
        )

        drawEventTitle(
            event,
            canvas,
            xPos,
            yPos + verticalOffset + currentDayEventPadding,
            bgRight - bgLeft - smallPadding,
            startDayIndex,
            endDayIndex
        )

        for (i in 0 until Math.min(event.daysCnt, 7 - event.startDayIndex % 7)) {
            dayVerticalOffsets.put(
                event.startDayIndex + i,
                verticalOffset.toInt() + eventTitleHeight + smallPadding * 2
            )
        }
    }

    private fun drawEventTitle(
        event: MonthViewEvent,
        canvas: Canvas,
        x: Float,
        y: Float,
        availableWidth: Float,
        startDay: DayMonthly,
        endDay: DayMonthly
    ) {
        val ellipsized =
            TextUtils.ellipsize(event.title, eventTitlePaint, availableWidth - smallPadding, TextUtils.TruncateAt.END)
        canvas.drawText(event.title, 0, ellipsized.length, x + smallPadding * 2, y, eventTitlePaint)
    }

    private fun getEventBackgroundColor(
        event: MonthViewEvent,
        startDayIndex: DayMonthly,
        endDayIndex: DayMonthly
    ): Paint {
        val backgroundPaint = Paint()
        backgroundPaint.color = event.color
        return backgroundPaint
    }

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        super.onTouchEvent(event)
//        Log.d("event", event.action.toString())
//        if (event.action == MotionEvent.ACTION_UP) {
//            xClickLocation = event.x
//            yClickLocation = event.y
//            dateSelectedCallback.invoke(getClickedDate())
//        }
//
//        return true
//    }


//    private fun getClickedDate(position:Int): DayMonthly {
//        val row = floor(yClickLocation!! / dayHeight).toInt()
//        val column = ceil(xClickLocation!! / dayWidth).toInt()
//        val position = (row) * (COLUMN_COUNT) + column
//        return days[position - 1]
//    }

    private fun getEventLastingDaysCount(event: Event): Int {
        val startDateTime = Formatter.getDateTimeFromTS(event.startTS)
        val endDateTime = Formatter.getDateTimeFromTS(event.endTS)
        val code = days.first().code
        val screenStartDateTime = Formatter.getDateTimeFromCode(code).toLocalDate()
        var eventStartDateTime = Formatter.getDateTimeFromTS(startDateTime.seconds()).toLocalDate()
        val eventEndDateTime = Formatter.getDateTimeFromTS(endDateTime.seconds()).toLocalDate()
        val diff = Days.daysBetween(screenStartDateTime, eventStartDateTime).days
        if (diff < 0) {
            eventStartDateTime = screenStartDateTime
        }
        return Days.daysBetween(eventStartDateTime, eventEndDateTime).days + 1
    }

    private fun initGrid(view:View) {
        view.month_grid.setPadding(0,weekDaysLetterHeight,0,0)
        view.month_grid.adapter = GridAdapter(context,dayWidth.toInt(),dayHeight.toInt())
        view.month_grid.onItemClickListener = AdapterView.OnItemClickListener{ parent, v, position, id ->
            dateSelectedCallback.invoke(days[position])
            Log.d("item",position.toString())
        }
    }
}

