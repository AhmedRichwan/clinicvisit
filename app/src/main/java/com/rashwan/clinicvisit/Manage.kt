package com.rashwan.clinicvisit

import android.app.AlertDialog
import android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.addnewtime.view.*
import kotlinx.android.synthetic.main.manage.*
import kotlinx.android.synthetic.main.set_dates.view.*
import kotlinx.android.synthetic.main.set_managers.view.*
import kotlinx.android.synthetic.main.set_managers.view.closeit
import java.text.SimpleDateFormat
import java.util.*

class Manage : AppCompatActivity() {
    var mRef: DatabaseReference? = null
    var mAuth: FirebaseAuth? = null
    val FirstDate = ToolsVisit.Dates(0)
    val LastDate = ToolsVisit.Dates(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        mRef = database.getReference("Times")
        mAuth = FirebaseAuth.getInstance()
        val maintimesarray = ToolsVisit.gettimes()
        val DaysInarray = ToolsVisit.DaysIn()
        val disabledtimesarray = ToolsVisit.disabledtimes()
        val mangersin = ToolsVisit.Getmgrs(1)
        val mangersout = ToolsVisit.Getmgrs(0)
        val textviewid = (R.id.tv)
        editmangers.setOnClickListener {
            ToolsVisit.btnanim(it)
            val alert = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val view = inflater.inflate(R.layout.set_managers, null)
            alert.setView(view)
            val alertdailoge = alert.create()
            alertdailoge.show()

            view.addmgrnow.setOnClickListener {
                val Temail = view.insertmail.text.toString().replace(" ", "")
                if ((Patterns.EMAIL_ADDRESS.matcher(Temail).matches())) {
                    mRef = database.getReference("Managers")
                    val id = ToolsVisit.MailtoStr(view.insertmail.text.toString())
                    val addmgr = Managers(id, view.insertmail.text.toString(), 1)
                    mRef!!.child(id).setValue(addmgr)
                        .addOnSuccessListener {
                            ToolsVisit.vtoast(
                                " تم اضافة المدير ",
                                1,
                                this,
                                layoutInflater
                            )
                        }
                        .addOnFailureListener {
                            ToolsVisit.vtoast(
                                "لم تتم اضافة المدير" + it.message!!,
                                3,
                                this,
                                layoutInflater
                            )
                        }


                } else {
                    ToolsVisit.vtoast(
                        " برجاء ادخال بريد الكتروني بشكل صحيح ثم اعادة المحاولة",
                        1,
                        this,
                        layoutInflater
                    )
                }
            }
            view.disablemgr.setOnClickListener {

                ToolsVisit.btnanim(it)
                if (mangersin.isEmpty()) {
                    ToolsVisit.vtoast(
                        "لا يوجد اي مدير مفعل يمكن ايقافه",
                        2,
                        this@Manage,
                        layoutInflater
                    )
                } else {

                    var selectedItems = mutableListOf<String>()
                    val final = Collections.unmodifiableList(selectedItems)
                    val builder = AlertDialog.Builder(this, THEME_DEVICE_DEFAULT_LIGHT)
                    builder.setTitle("اختر المدير المراد ايقافه")
                    builder.setIcon(R.drawable.icon_time_set)

                        .setMultiChoiceItems(mangersin.toTypedArray(), null,
                            DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                                if (isChecked) {
                                    selectedItems.add(mangersin.toTypedArray()[which])
                                } else if (selectedItems.contains(mangersin.toTypedArray()[which])) {
                                    selectedItems.remove(mangersin.toTypedArray()[which])
                                }
                            })
                        .setPositiveButton("OK",
                            DialogInterface.OnClickListener { dialog, id ->
//                            Toast.makeText(this,"you choose $final",Toast.LENGTH_LONG).show()
                                if (final.toString() != "[]") {


                                    val joinedarry =
                                        final.toString().replace("[", "").replace("]", "")
                                            .split(",").toTypedArray()
                                    var lastitemindex = joinedarry.size
                                    var timeormore = "المديرين"
                                    if (lastitemindex == 1) {
                                        timeormore = "المدير"
                                    }
                                    for (item in joinedarry) {
                                        try {
                                            var firstchar = item.substring(0, 1)
                                            var edited = item
                                            if (firstchar == " ") {
                                                edited = item.removeRange(0, 1)
                                            }
                                            mRef = database.getReference("Managers")

                                            mRef!!.child(ToolsVisit.MailtoStr(edited))
                                                .child("isenabled").setValue(0)
                                                .addOnSuccessListener {
                                                    if (item == joinedarry[lastitemindex - 1]) {
                                                        ToolsVisit.vtoast(
                                                            " تم ايقاف $timeormore  بنجاح   ",
                                                            1,
                                                            this@Manage,
                                                            layoutInflater
                                                        )
                                                    }


                                                }
                                                .addOnFailureListener {
                                                    ToolsVisit.vtoast(
                                                        " لم يتم الايقاف " + it.message,
                                                        3,
                                                        this@Manage,
                                                        layoutInflater
                                                    )


                                                }


                                        } catch (e: Exception) {
                                            ToolsVisit.vtoast(
                                                " لم يتم التفعيل " + e.message,
                                                3,
                                                this@Manage,
                                                layoutInflater
                                            )
                                        }
                                    }
                                }
                            })
                        .setNegativeButton("Cancel",
                            DialogInterface.OnClickListener { dialog, id ->
                            })
                    builder.create()
                    builder.show()
                }


            }
            view.enablemgr.setOnClickListener {

                ToolsVisit.btnanim(it)
                if (mangersout.isEmpty()) {
                    ToolsVisit.vtoast(
                        "لا يوجد اي مدير موقوف يمكن تفعيله",
                        2,
                        this@Manage,
                        layoutInflater
                    )
                } else {

                    var selectedItems = mutableListOf<String>()
                    val final = Collections.unmodifiableList(selectedItems)
                    val builder = AlertDialog.Builder(this, THEME_DEVICE_DEFAULT_LIGHT)
                    builder.setTitle("اختر المدير المراد تفعيله")
                    builder.setIcon(R.drawable.icon_time_set)

                        .setMultiChoiceItems(mangersout.toTypedArray(), null,
                            DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                                if (isChecked) {
                                    selectedItems.add(mangersout.toTypedArray()[which])
                                } else if (selectedItems.contains(mangersout.toTypedArray()[which])) {
                                    selectedItems.remove(mangersout.toTypedArray()[which])
                                }
                            })
                        .setPositiveButton("OK",
                            DialogInterface.OnClickListener { dialog, id ->
//                            Toast.makeText(this,"you choose $final",Toast.LENGTH_LONG).show()
                                if (final.toString() != "[]") {


                                    val joinedarry =
                                        final.toString().replace("[", "").replace("]", "")
                                            .split(",").toTypedArray()
                                    var lastitemindex = joinedarry.size
                                    var timeormore = "المديرين"
                                    if (lastitemindex == 1) {
                                        timeormore = "المدير"
                                    }
                                    for (item in joinedarry) {
                                        try {
                                            var firstchar = item.substring(0, 1)
                                            var edited = item
                                            if (firstchar == " ") {
                                                edited = item.removeRange(0, 1)
                                            }
                                            mRef = database.getReference("Managers")

                                            mRef!!.child(ToolsVisit.MailtoStr(edited))
                                                .child("isenabled").setValue(1)
                                                .addOnSuccessListener {
                                                    if (item == joinedarry[lastitemindex - 1]) {
                                                        ToolsVisit.vtoast(
                                                            " تم تفعيل $timeormore  بنجاح   ",
                                                            1,
                                                            this@Manage,
                                                            layoutInflater
                                                        )
                                                    }


                                                }
                                                .addOnFailureListener {
                                                    ToolsVisit.vtoast(
                                                        " لم يتم التفعيل " + it.message,
                                                        3,
                                                        this@Manage,
                                                        layoutInflater
                                                    )


                                                }


                                        } catch (e: Exception) {
                                            ToolsVisit.vtoast(
                                                " لم يتم التفعيل " + e.message,
                                                3,
                                                this@Manage,
                                                layoutInflater
                                            )
                                        }
                                    }
                                }
                            })
                        .setNegativeButton("Cancel",
                            DialogInterface.OnClickListener { dialog, id ->
                            })
                    builder.create()
                    builder.show()
                }


            }




            view.closeit.setOnClickListener {
                alertdailoge.dismiss()
            }


        }






        allreservations.setOnClickListener {
            ToolsVisit.btnanim(it)
            startActivity(Intent(this, AllReservation::class.java))

        }
        addtimebtn.setOnClickListener {
            ToolsVisit.btnanim(it)
            val alert = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val view = inflater.inflate(R.layout.addnewtime, null)
            alert.setView(view)
            val alertdailoge = alert.create()
            alertdailoge.show()
            view.closeit.setOnClickListener {
                alertdailoge.dismiss()
                startActivity(Intent(this, MainActivity::class.java))

            }
            view.pickthetime.setOnClickListener {


                val cal = Calendar.getInstance()
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        view.pickthetime.text =
                            SimpleDateFormat("h:mm a", Locale.getDefault()).format(cal.time)
                        if (view.pickthetime.text.toString().contains("M")) {
                            view.enableonceadded.visibility = View.VISIBLE
                            view.addtimenow.visibility = View.VISIBLE
                        }

                    }
                TimePickerDialog(
                    this,
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    false
                ).show()
            }
            view.addtimenow.setOnClickListener {

                try {
                    var timestat = 0
                    if (view.enableonceadded.isChecked) {
                        timestat = 1
                    }
                    mRef = database.getReference("Times")
                    mRef!!.child(view.pickthetime.text.toString()).setValue(timestat)

                        .addOnSuccessListener {
                            ToolsVisit.vtoast(
                                "تم اضافة الموعد بنجاح",
                                1,
                                this@Manage,
                                layoutInflater
                            )

                            view.addtimenow.visibility = View.GONE
                            view.enableonceadded.visibility = View.GONE
                            view.pickthetime.text = "اضافة موعد اخر ؟"
                        }
                        .addOnFailureListener {
                            ToolsVisit.vtoast(
                                "لم تتم اضافة الموعد" + it.message,
                                1,
                                this@Manage,
                                layoutInflater
                            )


                        }


                } catch (e: Exception) {
                    ToolsVisit.vtoast(
                        "لم تتم اضافة الموعد" + e.message,
                        1,
                        this@Manage,
                        layoutInflater
                    )

                }

            }
            view.closeit.setOnClickListener {
                alertdailoge.dismiss()
            }


        }
        enabletimesbtn.setOnClickListener {
            ToolsVisit.btnanim(it)
            if (disabledtimesarray.isEmpty()) {
                ToolsVisit.vtoast(
                    "لا يوجد اي مواعيد موقوفة يمكن تفعيلها",
                    2,
                    this@Manage,
                    layoutInflater
                )
            } else {

                var selectedItems = mutableListOf<String>()
                val final = Collections.unmodifiableList(selectedItems)
                val builder = AlertDialog.Builder(this, THEME_DEVICE_DEFAULT_LIGHT)
                builder.setTitle("اختر الموعد المراد تفعيله")
                builder.setIcon(R.drawable.icon_time_set)

                    .setMultiChoiceItems(disabledtimesarray.toTypedArray(), null,
                        DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                            if (isChecked) {
                                selectedItems.add(disabledtimesarray.toTypedArray()[which])
                            } else if (selectedItems.contains(disabledtimesarray.toTypedArray()[which])) {
                                selectedItems.remove(disabledtimesarray.toTypedArray()[which])
                            }
                        })
                    .setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, id ->
//                            Toast.makeText(this,"you choose $final",Toast.LENGTH_LONG).show()
                            if (final.toString() != "[]") {


                                val joinedarry =
                                    final.toString().replace("[", "").replace("]", "")
                                        .split(",").toTypedArray()
                                var lastitemindex = joinedarry.size
                                var timeormore = "المواعيد"
                                if (lastitemindex == 1) {
                                    timeormore = "الموعد"
                                }
                                for (item in joinedarry) {
                                    try {
                                        var firstchar = item.substring(0, 1)
                                        var edited = item
                                        if (firstchar == " ") {
                                            edited = item.removeRange(0, 1)
                                        }
                                        mRef = database.getReference("Times")
                                        mRef!!.child(edited).setValue(1)
                                            .addOnSuccessListener {
                                                if (item == joinedarry[lastitemindex - 1]) {
                                                    ToolsVisit.vtoast(
                                                        " تم تفعيل $timeormore  بنجاح   ",
                                                        1,
                                                        this@Manage,
                                                        layoutInflater
                                                    )
                                                }


                                            }
                                            .addOnFailureListener {
                                                ToolsVisit.vtoast(
                                                    " لم يتم التفعيل " + it.message,
                                                    3,
                                                    this@Manage,
                                                    layoutInflater
                                                )


                                            }


                                    } catch (e: Exception) {
                                        ToolsVisit.vtoast(
                                            " لم يتم التفعيل " + e.message,
                                            3,
                                            this@Manage,
                                            layoutInflater
                                        )
                                    }
                                }
                            }
                        })
                    .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                builder.create()
                builder.show()
            }


        }
        disabletimebtn.setOnClickListener {
            ToolsVisit.btnanim(it)
            if (maintimesarray.isEmpty()) {
                ToolsVisit.vtoast(
                    "لا يوجد اي مواعيد نشطة يمكن ايقافها",
                    2,
                    this@Manage,
                    layoutInflater
                )
            } else {
                val selectedItems = mutableListOf<String>()
                val final = Collections.unmodifiableList(selectedItems)
                val builder = AlertDialog.Builder(this, THEME_DEVICE_DEFAULT_LIGHT)
                builder.setIcon(R.drawable.icon_time_off)
                builder.setTitle("اختر الموعد المراد ايقافه")
                    .setMultiChoiceItems(maintimesarray.toTypedArray(), null,
                        DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                            if (isChecked) {
                                selectedItems.add(maintimesarray.toTypedArray()[which])
                            } else if (selectedItems.contains(maintimesarray.toTypedArray()[which])) {
                                selectedItems.remove(maintimesarray.toTypedArray()[which])
                            }
                        })
                    .setPositiveButton("تم",
                        DialogInterface.OnClickListener { dialog, id ->
//                            Toast.makeText(this,"you choose $final",Toast.LENGTH_LONG).show()
                            if (final.toString() != "[]") {

                                var joinedarry =
                                    final.toString().replace("[", "").replace("]", "")
                                        .split(",").toTypedArray()
                                var lastitemindex = joinedarry.size
                                var timeormore = "المواعيد"
                                if (lastitemindex == 1) {
                                    timeormore = "الموعد"
                                }
                                for (item in joinedarry) {
                                    try {
                                        var firstchar = item.substring(0, 1)
                                        var edited = item
                                        if (firstchar == " ") {
                                            edited = item.removeRange(0, 1)
                                        }
                                        mRef = database.getReference("Times")
                                        mRef!!.child(edited).setValue(0)
                                            .addOnSuccessListener {
                                                if (item == joinedarry[lastitemindex - 1]) {
                                                    ToolsVisit.vtoast(
                                                        " تم ايقاف $timeormore  بنجاح   ",
                                                        1,
                                                        this@Manage,
                                                        layoutInflater
                                                    )
                                                }
                                            }
                                            .addOnFailureListener {
                                                ToolsVisit.vtoast(
                                                    "   لم يتم الايقاف   ",
                                                    3,
                                                    this@Manage,
                                                    layoutInflater
                                                )
                                            }
                                    } catch (e: Exception) {
                                        ToolsVisit.vtoast(
                                            "   لم يتم الايقاف   " + e.message,
                                            3,
                                            this@Manage,
                                            layoutInflater
                                        )
                                    }
                                }
                            }
                        }
                    )
                    .setNegativeButton("الغاء",
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                builder.create()
                builder.show()
            }
        }
        setdatesbtn.setOnClickListener {
            ToolsVisit.btnanim(it)
            try {
                val alert = AlertDialog.Builder(this)
                val inflater = layoutInflater
                val view = inflater.inflate(R.layout.set_dates, null)
                try {
                    view.setFdate.text = Tools.epochToStr(FirstDate[0].toLong())
                    view.setLdate.text = Tools.epochToStr(LastDate[0].toLong())
                    alert.setView(view)
                } catch (e: Exception) {
                    ToolsVisit.vtoast(
                        "من فضلك تأكد من اتصالك بالانترنت ",
                        2,
                        this@Manage,
                        layoutInflater
                    )
                }

                val alertdailoge = alert.create()


                alertdailoge.show()
                view.closeit.setOnClickListener {
                    alertdailoge.dismiss()
                }
                view.setFdate.setOnClickListener {
                    ToolsVisit.PickLongDate(this, view.setFdate, layoutInflater)
                }
                view.setLdate.setOnClickListener {
                    ToolsVisit.PickLongDate(this, view.setLdate, layoutInflater)
                }
                view.confirmsettings.setOnClickListener {
                    try {
                        mRef = database.getReference("Dates")
                        mRef!!.child("FirstDate")
                            .setValue(Tools.strToEpoch(view.setFdate.text.toString()))
                        mRef!!.child("LastDate")
                            .setValue(Tools.strToEpoch(view.setLdate.text.toString()))
                            .addOnSuccessListener {
                                ToolsVisit.vtoast(
                                    "تم ضبط الفترة بنجاح",
                                    1,
                                    this@Manage,
                                    layoutInflater
                                )
                            }
                            .addOnFailureListener {
                                ToolsVisit.vtoast(
                                    "لم يتم ضبط الفترة" + it.message,
                                    1,
                                    this@Manage,
                                    layoutInflater
                                )
                            }
                    } catch (e: Exception) {
                        ToolsVisit.vtoast(
                            "لم يتم  ضبط الفترة" + e.message,
                            1,
                            this@Manage,
                            layoutInflater
                        )
                    }
                }
                view.closeit.setOnClickListener {
                    alertdailoge.dismiss()
                }

            } catch (e: Exception) {
            }
        }
        DaysInbtn.setOnClickListener {
            ToolsVisit.btnanim(it)
            val checkeditems = mutableListOf<String>()
            val uncheckeditems = mutableListOf<String>()
            var uncheckeddays: String = "initauncheck"
            var checkeddays: String = "initacheck"
            val BArray = ToolsVisit.DaysInBL()
            val final = Collections.unmodifiableList(checkeditems)
            val builder = AlertDialog.Builder(this, THEME_DEVICE_DEFAULT_LIGHT)
            builder.setIcon(R.drawable.icondate)
            builder.setTitle("اختر الايام التي سيكون بها الحجز متاحا")
                .setMultiChoiceItems(DaysInarray.toTypedArray(), BArray,
                    DialogInterface.OnMultiChoiceClickListener { _, which, isChecked ->
                        if (isChecked) {
                            checkeditems.add(DaysInarray.toTypedArray()[which])
                        } else if (checkeditems.contains(DaysInarray.toTypedArray()[which])) {
                            checkeditems.remove(DaysInarray.toTypedArray()[which])
                        }
                        checkeddays =
                            checkeditems.toString().replace("[", "").replace("]", "")
                                .split(",").toTypedArray().toString()


                        if (!isChecked) {
                            uncheckeditems.add(DaysInarray.toTypedArray()[which])
                        } else if (uncheckeditems.contains(DaysInarray.toTypedArray()[which])) {
                            uncheckeditems.remove(DaysInarray.toTypedArray()[which])
                        }

                        uncheckeddays =
                            uncheckeditems.toString().replace("[", "").replace("]", "")
                                .split(",").toTypedArray().toString()


                    }

                )
                .setPositiveButton("تم",
                    DialogInterface.OnClickListener { dialog, id ->
                        try {
                            if (checkeditems.toString() != "") {
                                var lastitemindex = checkeditems.size
                                for (item in checkeditems) {
                                    val firstchar = item.substring(0, 1)
                                    checkeddays = item
                                    if (firstchar == " ") {
                                        checkeddays = item.removeRange(0, 1)
                                    }

                                    try {
                                        if (!checkeddays.contains("[")) {
                                            mRef = database.getReference("DaysIn")
                                            mRef!!.child(checkeddays).setValue(1)
                                                .addOnSuccessListener {
                                                    if (item == checkeditems[lastitemindex - 1] && uncheckeditems.size == 0) {
                                                        ToolsVisit.vtoast(
                                                            " تم تعديل ايام العمل بنجاح   ",
                                                            1,
                                                            this@Manage,
                                                            layoutInflater
                                                        )
                                                    }


                                                }
                                                .addOnFailureListener {
                                                    ToolsVisit.vtoast(
                                                        "   لم يتم التعديل خطأ في قاعدة البيانات   ",
                                                        3,
                                                        this@Manage,
                                                        layoutInflater
                                                    )
                                                }
                                        }
                                    } catch (e: Exception) {
                                        ToolsVisit.vtoast(
                                            "   لم يتم التعديل - خطا في البرنامج   " + e.message,
                                            3,
                                            this@Manage,
                                            layoutInflater
                                        )

                                    }


                                }
                            }







                            if (uncheckeditems.toString() != "") {
                                for (item in uncheckeditems) {
                                    var lastitemindex = uncheckeditems.size
                                    val firstchar = item.substring(0, 1)
                                    uncheckeddays = item
                                    if (firstchar == " ") {
                                        uncheckeddays = item.removeRange(0, 1)
                                    }
                                    try {
                                        if (!uncheckeddays.contains("[")) {
                                            mRef = database.getReference("DaysIn")
                                            mRef!!.child(uncheckeddays).setValue(0)
                                                .addOnSuccessListener {
                                                    if (item == uncheckeditems[lastitemindex - 1]) {
                                                        ToolsVisit.vtoast(
                                                            " تم تعديل ايام العمل بنجاح   ",
                                                            1,
                                                            this@Manage,
                                                            layoutInflater
                                                        )
                                                    }

                                                }
                                                .addOnFailureListener {
                                                    ToolsVisit.vtoast(
                                                        "   لم يتم الايقاف   ",
                                                        3,
                                                        this@Manage,
                                                        layoutInflater
                                                    )
                                                }
                                        }
                                    } catch (e: Exception) {
                                        ToolsVisit.vtoast(
                                            "   لم يتم الايقاف   " + e.message,
                                            3,
                                            this@Manage,
                                            layoutInflater
                                        )
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            ToolsVisit.vtoast(
                                "   لم يتم الايقاف   " + e.message,
                                3,
                                this@Manage,
                                layoutInflater
                            )
                        }


                    })
                .setNegativeButton("الغاء",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            builder.create()
            builder.show()
        }
    }


}





