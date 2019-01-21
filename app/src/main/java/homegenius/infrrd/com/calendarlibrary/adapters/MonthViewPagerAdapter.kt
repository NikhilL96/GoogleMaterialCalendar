package homegenius.infrrd.com.calendarlibrary.adapters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import homegenius.infrrd.com.calendarlibrary.fragments.MonthViewFragment
import homegenius.infrrd.com.calendarlibrary.helper.DAY_CODE
import homegenius.infrrd.com.calendarlibrary.listeners.NavigationListener

class MonthViewPagerAdapter(fragmentManager: FragmentManager, private val codes: List<String>, private val listener: NavigationListener) : FragmentStatePagerAdapter(fragmentManager) {
    private val mFragments = SparseArray<MonthViewFragment>()

    override fun getCount() = codes.size

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        val code = codes[position]
        bundle.putString(DAY_CODE, code)

        val fragment = MonthViewFragment()
        fragment.arguments = bundle
        fragment.listener = listener

        mFragments.put(position, fragment)
        return fragment
    }

    fun updateCalendars(pos: Int) {
        for (i in -1..1) {
            mFragments[pos + i]?.updateCalendar()
        }
    }
}