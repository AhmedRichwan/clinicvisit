package com.rashwan.clinicvisit

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.bookvisit.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.GregorianCalendar as GregorianCalendar1


class BookVisit : AppCompatActivity() {

    var mRef: DatabaseReference? = null
    var cRef: DatabaseReference? = null
    var BookedList: ArrayList<Booked>? = null
    var mAuth: FirebaseAuth? = null
    val maintimesarray = ToolsVisit.gettimes()
    var GFA = mutableListOf<String>()
    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var DaysInNumbersArr: MutableList<Int> = mutableListOf()
    val FirstDate = ToolsVisit.Dates(0)
    val LastDate = ToolsVisit.Dates(1)
    var timeset = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bookvisit)
        ToolsVisit.get_Cinfo(this, null, null, null, null, null, null, vnumberTV)
        mRef = database.getReference("Booked")
        cRef = database.getReference("Config")
        mAuth = FirebaseAuth.getInstance()
        DaysInNumbersArr = ToolsVisit.DaysInNumbers()
        val textviewid = (R.id.tv)
        val patsexadapter = ArrayAdapter(
            this@BookVisit,
            R.layout.spstyle,
            textviewid,
            arrayOf("غير محدد", "ذكر", "انثى")
        )
        patsex.adapter = patsexadapter
        setDTbtn.setOnClickListener {
            ToolsVisit.btnanim(it)
            pickDateValidation(pickday)

        }

/*
        fullbooked() {
            mRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var vacant: MutableList<String> = mutableListOf()
                    var bookedlist: MutableList<String> = mutableListOf()
                    vacant.addAll(maintimesarray)
                    var wantedvisit = "notexist"
                    for (n in dataSnapshot.children) {
                        val visita = n.getValue(Booked::class.java)
                        var pickedvisitdate = visita?.visitdate?.toLong()
                        if (pickedvisitdate == tools.strToEpoch(pickday.text.toString())) {
                            wantedvisit = visita?.time.toString()
                        }

                        if (vacant.contains(wantedvisit)) {
                            vacant.remove(wantedvisit)
                            bookedlist.add(wantedvisit)

                        } else {


                        }


                    }
                    val adapter =
                        ArrayAdapter(this@bookvisit, R.layout.spstyle, (R.id.tv), bookedlist)
                    fullbookedsp.adapter = adapter
                }


                override fun onCancelled(databaseError: DatabaseError) {
                    ToolsVisit.vtoast(
                        "حدث خطأ",
                        3,
                        this@bookvisit,
                        layoutInflater)
                    println("loadPost:onCancelled ${databaseError.toException()}")

                }
            })


        }
*/
        booknow.setOnClickListener {
            ToolsVisit.btnanim(it)
            booknow()
        }
        backtomain.setOnClickListener {
            ToolsVisit.btnanim(it)
            startActivity(Intent(this, MainActivity::class.java))
        }
        pickVtime.setOnClickListener {
            ToolsVisit.btnanim(it)
            pickDateValidation(pickday)
//             AvailableDialoge(tools.strToEpoch(pickday.text.toString()),pickVtime,pickday)
        }
    }

    fun booknow() {
//validate the book number first
        if (vnumberTV.text.isEmpty()) {
            ToolsVisit.get_Cinfo(this, null, null, null, null, null, null, vnumberTV)

        }


        if (pickday.text.toString().length > 14 && vnumberTV.text.isNotEmpty() && pickday.text.toString()
                .contains("2") && patname.text.isNotEmpty() && patphone.text.length > 7
        ) {

            val id = mRef!!.push().key!!
            try {
                val mybooking =
                    Booked(
                        id,
                        vnumberTV.text.toString(),
                        Tools.strToEpoch(pickday.text.toString()),
                        0,
                        pickday.text.toString(),
                        (LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd EEEE HH-mm")
                        )),
                        pickVtime.text.toString(),
                        "",
                        0,
                        0,
                        0,
                        0,
                        patname.text.toString(),
                        patemail.text.toString(),
                        patphone.text.toString(),
                        patage.text.toString(),
                        patsex.selectedItemPosition,
                        patnote.text.toString(),
                        0.0,
                        "paymentreference",
                        mAuth?.currentUser?.email.toString()
                    )
                mRef!!.child(id).setValue(mybooking)
                    .addOnSuccessListener {
                        ToolsVisit.vtoast(
                            " تم ارسال طلب الحجز برجاء انتظار رسالة التأكيد",
                            1,
                            this@BookVisit,
                            layoutInflater
                        )
                        cRef?.child("vnumber")?.setValue(vnumberTV.text.toString().toInt() + 1)
                    }
                    .addOnFailureListener {
                        ToolsVisit.vtoast(
                            "لم تتم اضافة الموعد" + it.message!!,
                            3,
                            this@BookVisit,
                            layoutInflater
                        )
                    }
                startActivity(Intent(this, MainActivity::class.java))


            } catch (e: Exception) {
                ToolsVisit.vtoast(e.message!!, 2, this@BookVisit, layoutInflater)
            }
        } else {
            ToolsVisit.vtoast(
                "تأكد من اكمال البيانات الاساسية وهي تاريخ ووقت الزيارة والاسم ورقم الهاتف ",
                2,
                this@BookVisit,
                layoutInflater
            )

        }
    }

    fun pickDateValidation(textview: TextView): Long {
        var orginalvalue = textview.text
        val c = Calendar.getInstance()
        var result: Long = 0
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val myCalendar = GregorianCalendar1(year, month, dayOfMonth)
                val dayOfWeek = myCalendar.get(Calendar.DAY_OF_WEEK)
                if (dayOfWeek !in DaysInNumbersArr) {
                    ToolsVisit.vtoast(
                        "عفوا ، لا يوجد مواعيد متاحه بهذا اليوم حيث انه يوم عطلة اسبوعية بالعيادة ".toString(),
//                        DaysInNumbersArr.toString(),
                        3,
                        this@BookVisit,
                        layoutInflater
                    )
                    pickDateValidation(pickday)
                } else {
                    val a = myCalendar.timeInMillis.toString()
                    result = myCalendar.timeInMillis.toString().substring(0, 10).toLong()
                    textview.text = Tools.epochToStr(result)
                    AvailableDialoge(result, pickVtime, textview)
//                    pickVtime.text = getString(R.string.PickVtime)
                    pickVtime.visibility = View.VISIBLE
                }
            },
            1,
            2,
            3
        )
        try {
            dpd.datePicker.firstDayOfWeek = Calendar.SUNDAY
//        dpd.datePicker.minDate = (c.timeInMillis)  //today's date in millisec
            dpd.datePicker.minDate = (FirstDate[0] + "000").toLong()
            dpd.datePicker.maxDate = (LastDate[0] + "000").toLong()

            dpd.show()
        } catch (e: Exception) {
            ToolsVisit.vtoast(
                "من فضلك تأكد من اتصالك بالانترنت ",
                2,
                this@BookVisit,
                layoutInflater
            )
        }
        return result
    }

    fun AvailableDialoge(Date: Long, TextView: TextView, DateTextView: TextView): String {
        var result = "null val"
        val BRef = database.getReference("Booked")
        BRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    timeset.clear()
                    timeset.addAll(maintimesarray)
                    var wantedvisit = ""
                    var sugtimevisit = ""
                    for (n in dataSnapshot.children) {
                        val visita = n.getValue(Booked::class.java)
                        if (Date == visita?.visitdate?.toLong()) {
                            wantedvisit = visita.time.toString()
                            sugtimevisit = visita.sugtime.toString()
                        }
                        if (timeset.contains(wantedvisit)) {
                            timeset.remove(wantedvisit)
                        }
                        if (timeset.contains(sugtimevisit)) {
                            timeset.remove(sugtimevisit)
                        }
                    }
                    val mBuilder = AlertDialog.Builder(
                        this@BookVisit,
                        AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
                    )
                    mBuilder.setTitle(" المواعيد المتوفرة في يوم: ${pickday.text} ")
                    mBuilder.setIcon(R.drawable.icon_time_set)
                    mBuilder.setSingleChoiceItems(
                        timeset.toTypedArray(),
                        0
                    ) { dialogInterface, i ->
                    }
                    mBuilder.setPositiveButton(getString(R.string.choose)) { dialog, which ->
                        val position = (dialog as AlertDialog).listView.checkedItemPosition
                        if (position != -1 && timeset.size != 0) {
                            TextView.text = timeset[position]
                            result = timeset[position]
                        }
                    }
                    mBuilder.setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                        if (!TextView.text.contains(":")) {
                            TextView.text = getString(R.string.time)
                            DateTextView.text = getString(R.string.visitdate)
                        }
                        dialog.cancel()
                    }
                    val mDialog: AlertDialog = mBuilder.create()
                    if (Date.toString().length > 10 && timeset.size == 0) {
                        ToolsVisit.vtoast(
                            "عفوا ، لا يوجد مواعيد متاحة في هذا اليوم",
                            2,
                            this@BookVisit,
                            layoutInflater
                        )
                    } else if (Date.toString().length < 10) {
                        ToolsVisit.vtoast(
                            "اختر اليوم اولا",
                            2,
                            this@BookVisit,
                            layoutInflater
                        )
                    } else {
                        mDialog.show()
                    }
                } catch (e: Exception) {
                    ToolsVisit.vtoast(
                        e.message!!,
                        2,
                        this@BookVisit,
                        layoutInflater
                    )
                    timeset.clear()
                }
            }
        })
        return result
    }
}
