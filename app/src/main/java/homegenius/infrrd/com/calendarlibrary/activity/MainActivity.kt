package homegenius.infrrd.com.calendarlibrary.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import homegenius.infrrd.com.calendarlibrary.R
import homegenius.infrrd.com.calendarlibrary.fragments.DayFragmentsHolder
import homegenius.infrrd.com.calendarlibrary.fragments.MonthFragmentHolder
import homegenius.infrrd.com.calendarlibrary.helper.DAY_CODE
import homegenius.infrrd.com.calendarlibrary.helper.Formatter
import org.joda.time.DateTime

class MainActivity : AppCompatActivity() {

    private var currentFragments = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateViewPager()
    }

    private fun updateViewPager(dayCode:String? = Formatter.getTodayCode()) {
        val fragment = MonthFragmentHolder()
        currentFragments.forEach {
            supportFragmentManager.beginTransaction().remove(it).commitNow()
        }
        currentFragments.clear()
        currentFragments.add(fragment)
        val bundle = Bundle()

        bundle.putString(DAY_CODE, dayCode)

        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().add(R.id.fragments_holder, fragment).commitNow()
    }

    fun openDayFromMonthly(dateTime: DateTime) {
        if (currentFragments.last() is DayFragmentsHolder) {
            return
        }

        val fragment = DayFragmentsHolder()
        currentFragments.add(fragment)
        val bundle = Bundle()
        bundle.putString(DAY_CODE, Formatter.getDayCodeFromDateTime(dateTime))
        fragment.arguments = bundle
        try {
            supportFragmentManager.beginTransaction().add(R.id.fragments_holder, fragment).commitNow()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } catch (e: Exception) {
        }
    }
}
