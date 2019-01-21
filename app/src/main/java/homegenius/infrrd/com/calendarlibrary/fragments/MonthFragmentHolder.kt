package homegenius.infrrd.com.calendarlibrary.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import homegenius.infrrd.com.calendarlibrary.R
import homegenius.infrrd.com.calendarlibrary.adapters.MonthViewPagerAdapter
import homegenius.infrrd.com.calendarlibrary.helper.DAY_CODE
import homegenius.infrrd.com.calendarlibrary.helper.Formatter
import homegenius.infrrd.com.calendarlibrary.listeners.NavigationListener
import kotlinx.android.synthetic.main.month_view_pager_layout.view.*

class MonthFragmentHolder : Fragment(), NavigationListener {
    private val PREFILLED_MONTHS = 251

    private var viewPager: ViewPager? = null
    private var defaultMonthlyPage = 0
    private var todayDayCode = ""
    private var currentDayCode = ""
    private var isGoToTodayVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentDayCode = arguments?.getString(DAY_CODE) ?: ""
        todayDayCode = Formatter.getTodayCode()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.month_view_pager_layout, container, false)
        viewPager = view.view_pager
        viewPager!!.id = (System.currentTimeMillis() % 100000).toInt()
        setupFragment()
        return view
    }

    private fun setupFragment() {
        val codes = getMonths(currentDayCode)
        defaultMonthlyPage = codes.size / 2
        activity?.supportFragmentManager?.let {
            val monthlyAdapter = MonthViewPagerAdapter(it, codes, this)
            viewPager?.apply {
                adapter = monthlyAdapter
                currentItem = defaultMonthlyPage
            }
        }
    }

    private fun getMonths(code: String): List<String> {
        val months = ArrayList<String>(PREFILLED_MONTHS)
        val today = Formatter.getDateTimeFromCode(code).withDayOfMonth(1)
        for (i in -PREFILLED_MONTHS / 2..PREFILLED_MONTHS / 2) {
            months.add(Formatter.getDayCodeFromDateTime(today.plusMonths(i)))
        }
        Log.d("months", months.toString())

        return months
    }


    override fun onNext() {
        viewPager?.let {
            it.currentItem = it.currentItem + 1
        }
    }

    override fun onPrev() {
        viewPager?.let {
            it.currentItem = it.currentItem - 1
        }
    }

}