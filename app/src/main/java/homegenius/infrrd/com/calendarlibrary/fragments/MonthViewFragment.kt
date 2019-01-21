package homegenius.infrrd.com.calendarlibrary.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.gson.Gson
import homegenius.infrrd.com.calendarlibrary.R
import homegenius.infrrd.com.calendarlibrary.activity.DateDetailsActivity
import homegenius.infrrd.com.calendarlibrary.helper.DAY_CODE
import homegenius.infrrd.com.calendarlibrary.helper.DAY_DETAILS_CODE
import homegenius.infrrd.com.calendarlibrary.helper.Formatter
import homegenius.infrrd.com.calendarlibrary.helper.MonthlyCalendarData
import homegenius.infrrd.com.calendarlibrary.listeners.MonthlyCalendar
import homegenius.infrrd.com.calendarlibrary.listeners.NavigationListener
import homegenius.infrrd.com.calendarlibrary.model.DayMonthly
import homegenius.infrrd.com.calendarlibrary.model.Event
import kotlinx.android.synthetic.main.month_view_layout.*
import kotlinx.android.synthetic.main.month_view_layout.view.*
import kotlinx.android.synthetic.main.navigator.view.*
import org.joda.time.DateTime
import android.util.TypedValue



class MonthViewFragment : Fragment(), MonthlyCalendar {

    lateinit var listener: NavigationListener
    lateinit var holder: ConstraintLayout
    private var dayCode: String = ""
    private var calendar:MonthlyCalendarData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(homegenius.infrrd.com.calendarlibrary.R.layout.month_view_layout, container, false)
        holder = view.month_calendar_holder
        arguments?.let {
            dayCode = it.getString(DAY_CODE) as String
        }

        setupButtons()
        context?.let {
            calendar = MonthlyCalendarData(this, it)
        }
        view.month_view.assignGridView(view.month_grid)
        return view
    }


    fun updateCalendar() {
        calendar?.updateMonthlyCalendar(Formatter.getDateTimeFromCode(dayCode))
    }

    private fun setupButtons() {
        holder.navigation_buttons.next.apply {
            setOnClickListener {
                listener.onNext()
            }
        }

        holder.navigation_buttons.prev.apply {
            setOnClickListener {
                listener.onPrev()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        calendar?.apply {
            targetDate = Formatter.getDateTimeFromCode(dayCode)
            getDays(false)
        }
        updateCalendar()
    }

    override fun updateMonthlyCalendar(
        context: Context,
        month: String,
        days: ArrayList<DayMonthly>,
        checkedEvents: Boolean,
        currTargetDate: DateTime
    ) {
        activity?.runOnUiThread {
            holder.navigation_buttons.month_text.apply {
                text = month
            }
            updateDays(days)
        }
    }



    private fun updateDays(days:ArrayList<DayMonthly>) {
        holder.month_view.updateDays(days) {
            val intent = Intent(context,DateDetailsActivity::class.java)
            intent.putExtra(DAY_DETAILS_CODE,Gson().toJson(it))
            startActivity(intent)
        }
    }

}

class GridAdapter(private val context: Context?,val width:Int, val height:Int) : BaseAdapter() {
    override fun getCount(): Int {
        return 42
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0L
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View = View(context)
        val outValue = TypedValue()
        view.layoutParams = ViewGroup.LayoutParams(width,height)
        context?.theme?.resolveAttribute(android.R.attr.selectableItemBackground,outValue,true)
        view.setBackgroundResource(outValue.resourceId)
        return view
    }
}