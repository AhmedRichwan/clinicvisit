package com.rashwan.clinicvisit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.rowstyle.view.*
import java.time.LocalDate
import java.util.*

class visitadapter(context: Context, caseList: List<Booked>) :
    ArrayAdapter<Booked>(context, 0, caseList) {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = View.inflate(context, R.layout.rowstyle, null)
        val book: Booked? = getItem(position)
        view.vnumber.text = book?.vnumber.toString()
        view.name.text = book?.patname.toString()
        view.patphone.text = book?.patphone
        view.time.text = book?.time
        view.Vdate.text = Tools.epochToStr(book?.visitdate!!.toLong())
        when {

            (book.isconfirmed == 0) -> {
                view.status.text = "انتظار"
                view.status.background = ContextCompat.getDrawable(context, R.color.myYellow)

            }
            (book.isconfirmed == 1) -> {
                view.status.text = "مؤكدة"
                view.status.background = ContextCompat.getDrawable(context, R.color.mygreen)
            }
            (book.isconfirmed == 2) -> {
                view.status.text = "تمت"
                view.status.background = ContextCompat.getDrawable(context, R.color.myblackLight)
            }

            (book.isconfirmed == 4) -> {
                view.status.text = "ملغية"
                view.status.background = ContextCompat.getDrawable(context, R.color.honeyl)

            }

            (book.isconfirmed == 5) -> {
                view.status.text = "طلب تعديل موعد"
                view.status.background = ContextCompat.getDrawable(context, R.color.mypink)

            }

            (book.isconfirmed == 6) -> {
                view.status.text = "طلب الغاء"
                view.status.background = ContextCompat.getDrawable(context, R.color.myreds)

            }
        }
        val todayinsec = Calendar.getInstance().timeInMillis.toString()
            .substring(0, Calendar.getInstance().timeInMillis.toString().length - 3)
            .toLong() - 86400
        if (book.isconfirmed != 2 && book.visitdate!! < todayinsec) {
            view.status.text = "فائتة"

        }
        if (book.isdeleted == 1) {
            view.status.text = "محذوفة"

        }


        //remaining
        var diffDays =
            ((book.visitdate!!.toLong() - Tools.strToEpoch(LocalDate.now().toString())) / (86400))
        var daycount = "ايام"
        if (diffDays.toInt() > 11) {
            daycount = "يوما"
        }
        when {
            (diffDays.toInt() == 0) -> view.remain.text = "موعد اليوم!"
            (diffDays.toInt() == 1) -> view.remain.text = "موعد باكر!"
            (diffDays.toInt() == 2) -> view.remain.text = "موعد بعد باكر!"
            (diffDays.toInt() == 3) -> view.remain.text = "متبقي يومان"
            (diffDays.toInt() > 0) -> view.remain.text = "متبقي( $diffDays ) $daycount"
            (diffDays.toInt() < -10) -> {
                daycount = "يوما"
                diffDays *= -1
                view.remain.text = "مضى( " + diffDays.toString() + " )$daycount"
            }
            (diffDays.toInt() == -1) -> view.remain.text = "موعد الأمس"
            (diffDays.toInt() == -2) -> view.remain.text = "مضى يومان"
            (diffDays.toInt() < -2) -> {
                diffDays = -diffDays
                view.remain.text = "مضى( " + diffDays.toString() + " )$daycount"
            }
        }
        view.remain.text = view.remain.text.toString() + " ⏳"
        if (book.isdeleted == 1) {
//            view.rowstyle.background =
//                ContextCompat.getDrawable(context, R.drawable.background_rowstyle_deleted)
//            view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
        } else {
//            view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale))
        }

        //remaining
        return view
    }
}
