package homegenius.infrrd.com.calendarlibrary.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import com.google.gson.Gson
import homegenius.infrrd.com.calendarlibrary.R
import homegenius.infrrd.com.calendarlibrary.helper.DAY_DETAILS_CODE
import homegenius.infrrd.com.calendarlibrary.helper.Formatter
import homegenius.infrrd.com.calendarlibrary.model.DayMonthly
import kotlinx.android.synthetic.main.activity_date_details.*
import org.joda.time.DateTime

class DateDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_details)

        val dateDetails: DayMonthly? = Gson().fromJson(intent.getStringExtra(DAY_DETAILS_CODE),DayMonthly::class.java)
        lateinit var dateDetailsDateObject: DateTime
        dateDetails?.code?.let {
            dateDetailsDateObject = Formatter.getDateTimeFromCode(it)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        date_heading.text = dateDetailsDateObject.monthOfYear().asText + " " + dateDetailsDateObject.dayOfMonth().asShortText

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

}
