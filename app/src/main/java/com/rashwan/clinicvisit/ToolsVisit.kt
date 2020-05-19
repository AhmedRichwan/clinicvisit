package com.rashwan.clinicvisit

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.mtoast.view.*
import java.util.*


class ToolsVisit {


    companion object {
        fun Notify(bookid: String, name: String, description: String, context: Context) {
            val id = context.applicationContext.packageName
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notifyIntent = Intent(context, BookDetails::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            notifyIntent.putExtra("id", bookid)
            notifyIntent.putExtra("isnotify", 1)
            val notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val channel = NotificationChannel(id, name, importance)
            channel.description = description
            channel.enableLights(true)
            channel.lightColor = Color.GREEN
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
            val notificationID = 101
            val notification = Notification.Builder(
                context,
                id
            )
                .setAutoCancel(true)
                .setContentTitle(name)
                .setContentText(description)
                .setSmallIcon(R.drawable.visit_icon)
                .setChannelId(id)
                .setContentIntent(notifyPendingIntent)
                .build()
            val admins: MutableList<String> = mutableListOf()
            val mAuth = FirebaseAuth.getInstance()
            val currentlogged = mAuth.currentUser?.email.toString()
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val mRef = database.getReference("Managers")
            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    admins.clear()
                    for (n in dataSnapshot.children) {

                        val mgr = n.getValue(Managers::class.java)!!
                        if (mgr.isenabled!! == 1) {
                            admins.add(mgr.mgremail!!)
                        }
                        if (currentlogged in admins) notificationManager.notify(
                            notificationID,
                            notification
                        )

                    }
                }
            })
        }
        fun Availables(
            pickedvisitdate: Long,
            context: android.content.Context,
            layoutInflater: LayoutInflater
        ): MutableList<String> {
            val vacant: MutableList<String> = mutableListOf()

            var enabledlist: MutableList<String> = mutableListOf()
            var mRef: DatabaseReference? = null
            var mAuth: FirebaseAuth? = null
            mAuth = FirebaseAuth.getInstance()
            val availabletimes: MutableList<String> = mutableListOf()
            var TRef: DatabaseReference? = null
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            TRef = database.getReference("Times")
            TRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    ToolsVisit.vtoast(
                        " حدث خطأ في الاتصال بالانترنت",
                        3,
                        context,
                        layoutInflater
                    )
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        vacant.clear()
                        for (n in dataSnapshot.children) {
                            if (n.value.toString() == "1") {
                                vacant.add(n.key.toString())
                            }
                        }
                    } catch (e: Exception) {
                        ToolsVisit.vtoast(
                            " حدث خطأ",
                            3,
                            context,
                            layoutInflater
                        )
                    }
                }
            })


/*
            val   BRef = database.getReference("Booked")
            BRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    ToolsVisit.vtoast(
                        " حدث خطأ في الاتصال بالانترنت",
                        3,
                        context,
                        layoutInflater
                    )
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        availabletimes.clear()
                        availabletimes.addAll(alltimes)
                        var wantedvisit = ""
                        for (n in dataSnapshot.children) {
                            val visita = n.getValue(Booked::class.java)
                            if (pickedvisitdate == visita?.visitdate?.toLong()) {
                                wantedvisit = visita.time.toString()
                            }
                            if (availabletimes.contains(wantedvisit)) {
                                availabletimes.remove(wantedvisit)
                            }

                        }
                    } catch (e: Exception) {
                        ToolsVisit.vtoast(
                            " حدث خطأ ",
                            3,
                            context,
                            layoutInflater
                        )
                        availabletimes.clear()
                    }
                }

            })
            */

            vacant.add("test test")
            return vacant
        }

        fun gettimes(): MutableList<String> {
            val vacant: MutableList<String> = mutableListOf()
            var enabledlist: MutableList<String> = mutableListOf()
            var mRef: DatabaseReference? = null
            var mAuth: FirebaseAuth? = null
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            mRef = database.getReference("Times")
            mAuth = FirebaseAuth.getInstance()

            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    vacant.clear()
                    for (n in dataSnapshot.children) {
                        val tindex = n.key.toString()
                        if (n.value.toString() == "1") {

                            vacant.add(tindex)
                        }
                    }
                }
            })
            Thread.sleep(500)
            return vacant

        }

        fun GoExpired() {
            //this function to :
            // make every book which it's visit date in the past Marked as Missed
            //confirm the regvisit date is the same  to  visit date in the string format

            var mRef: DatabaseReference? = null
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            mRef = database.getReference("Booked")
            mRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val today = Calendar.getInstance().timeInMillis.toString()
                    val todayinsec = Calendar.getInstance().timeInMillis.toString()
                        .substring(0, Calendar.getInstance().timeInMillis.toString().length - 3)
                        .toLong()
                    for (n in dataSnapshot.children) {

                        val book = n.getValue(Booked::class.java)!!
                        val id = book.id.toString()
                        val visitdatevalue = Tools.epochToStr(book.visitdate!!.toLong())
                        mRef.child(id).child("regvisitdate").setValue(visitdatevalue)
                        if (book.visitdate!! < todayinsec) {
                            val exbook = n.getValue(Booked::class.java)!!
                            val id = exbook.id.toString()
                            mRef.child(id).child("isconfirmed").setValue(3)
                        }
                    }
                }
            })

        }

        fun btnanim(view: View) {
            view.setBackgroundResource(R.drawable.gradient_animation)
            val animDrawable = view.background as AnimationDrawable
            animDrawable.setEnterFadeDuration(1)
            animDrawable.setExitFadeDuration(2000)
            animDrawable.isOneShot = true
            animDrawable.start()
        }

        fun disabledtimes(): MutableList<String> {
            var vacant: MutableList<String> = mutableListOf()
            var enabledlist: MutableList<String> = mutableListOf()
            var mRef: DatabaseReference? = null
            var mAuth: FirebaseAuth? = null
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            mRef = database.getReference("Times")
            mAuth = FirebaseAuth.getInstance()

            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    enabledlist.clear()
                    vacant.clear()
                    for (n in dataSnapshot.children) {
                        //  val tindex = n.getValue(Long.toString()::class.java)
                        val tindex = n.key.toString()
                        if (n.value.toString() == "0") {
                            vacant.add(tindex)
                        }
                        //                       enabledlist.add(n.value.toString())

                    }


                }
            })
            return vacant
        }

        fun Getmgrs(type: Int): MutableList<String> {
            val vacant: MutableList<String> = mutableListOf()
            val mAuth = FirebaseAuth.getInstance()
            val currentlogged = mAuth.currentUser?.email.toString()
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val mRef = database.getReference("Managers")
            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    vacant.clear()
                    for (n in dataSnapshot.children) {
                        val mgr = n.getValue(Managers::class.java)!!
                        if (mgr.isenabled!! == type) {
                            vacant.add(mgr.mgremail!!)

                        }
                    }
                }
            })
            return vacant
        }

        fun isAdmin(
            progressbar: View?,
            showforadmins: View?,
            hidefromadmins: View?
        ): MutableList<String> {
//            var btn= view as Button
            progressbar?.visibility = View.VISIBLE
            val admins: MutableList<String> = mutableListOf()
            val mAuth = FirebaseAuth.getInstance()
            val currentlogged = mAuth.currentUser?.email.toString()
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val mRef = database.getReference("Managers")
            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    admins.clear()
                    for (n in dataSnapshot.children) {
                        val mgr = n.getValue(Managers::class.java)!!
                        if (mgr.isenabled!! == 1) {
                            admins.add(mgr.mgremail!!)
                        }
                        if (currentlogged in admins) {
                            showforadmins?.visibility = View.VISIBLE
                            hidefromadmins?.visibility = View.GONE
                        }
                    }
                }
            })
            progressbar?.visibility = View.INVISIBLE
            return admins
        }

        fun isManager(): Boolean {

            val admins: MutableList<String> = mutableListOf()
            val mAuth = FirebaseAuth.getInstance()
            val currentlogged = mAuth.currentUser?.email.toString()
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val mRef = database.getReference("Managers")
            var ismanger = false
            mRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    admins.clear()
                    for (n in dataSnapshot.children) {

                        val mgr = n.getValue(Managers::class.java)!!
                        if (mgr.isenabled!! == 1) {
                            admins.add(mgr.mgremail!!)
                        }
                        if (currentlogged in admins) ismanger = true
                    }
                }
            })
            return ismanger
        }

        fun DaysIn(): MutableList<String> {
            var vacant: MutableList<String> = mutableListOf()
            var enabledlist: MutableList<String> = mutableListOf()
            var mRef: DatabaseReference? = null
            var mAuth: FirebaseAuth? = null
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            mRef = database.getReference("DaysIn")
            mAuth = FirebaseAuth.getInstance()

            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    enabledlist.clear()
                    vacant.clear()
                    for (n in dataSnapshot.children) {
                        //  val tindex = n.getValue(Long.toString()::class.java)
                        val tindex = n.key.toString()
                        if (n.value.toString() == "0" || n.value.toString() == "1") {
                            vacant.add(tindex)
                        }
                        //                       enabledlist.add(n.value.toString())

                    }


                }
            })
            return vacant
        }

        fun DaysInBL(): BooleanArray {
            var vacant = BooleanArray(7)
            var mRef: DatabaseReference? = null
            var mAuth: FirebaseAuth? = null
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            mRef = database.getReference("DaysIn")
            mAuth = FirebaseAuth.getInstance()
            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //                    vacant.clear()
                    var i = 0
                    var bol: Boolean
                    for (n in dataSnapshot.children) {
                        bol = n.value.toString() == "1"
                        vacant[i] = (bol)
                        i += 1

                    }
                }
            })
            return vacant
        }

        fun Dates(R: Int): MutableList<String> {
            var vacant: MutableList<String> = mutableListOf()
            var enabledlist: MutableList<String> = mutableListOf()
            var mRef: DatabaseReference? = null
            var mAuth: FirebaseAuth? = null
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            mRef = database.getReference("Dates")
            mAuth = FirebaseAuth.getInstance()

            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    enabledlist.clear()
                    vacant.clear()
                    for (n in dataSnapshot.children) {
                        val tindex = n.value.toString()
                        if (n.key.toString() == "FirstDate" && R == 0) {
                            vacant.add(tindex)
                        }
                        if (n.key.toString() == "LastDate" && R == 1) {
                            vacant.add(tindex)
                        }

                    }


                }
            })
            return vacant
        }


        fun GetVNumber(): Int {
            var vnumber: Int = 99
            var mRef: DatabaseReference? = null
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            mRef = database.getReference("Config")
            mRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    vnumber = dataSnapshot.child("vnumber").value.toString().toInt()

                    mRef.child("vnumber").setValue(vnumber + 1)

                }

            })


            return vnumber
        }


        fun get_Cinfo(
            context: Context,
            mainview: View?,
            progressbar: ProgressBar?,
            cname: TextView?,
            caddress: TextView?,
            cphone: TextView?,
            cweb: TextView?,
            vnumber: TextView?
        ) {
            val ProgressAction = NetworkTask(context as Activity)
            ProgressAction.execute()
            mainview?.visibility = View.GONE
//            progressbar?.visibility=View.VISIBLE
            var vacant: MutableList<String> = mutableListOf()
            var enabledlist: MutableList<String> = mutableListOf()
            var mRef: DatabaseReference? = null
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            mRef = database.getReference("Config")
            mRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (n in dataSnapshot.children) {
                        if (n.key.toString() == "name") cname?.text = n.value.toString()
                        if (n.key.toString() == "address") caddress?.text = n.value.toString()
                        if (n.key.toString() == "phone") cphone?.text = n.value.toString()
                        if (n.key.toString() == "website") cweb?.text = n.value.toString()
                        if (n.key.toString() == "vnumber") vnumber?.text = n.value.toString()
                        if (vnumber?.text != "") ProgressAction.cancel(true)
                    }
                    mainview?.visibility = View.VISIBLE
//                    mainview?.startAnimation(AnimationUtils.loadAnimation(context, R.anim.sequential))

                }
            })

        }

        fun set_Cinfo(
            cname: TextView,
            caddress: TextView,
            cphone: TextView,
            cweb: TextView,
            context: android.content.Context,
            layoutInflater: LayoutInflater
        ) {
            var vacant: MutableList<String> = mutableListOf()
            var enabledlist: MutableList<String> = mutableListOf()
            var mRef: DatabaseReference? = null
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            mRef = database.getReference("Config")
            if (cname.text.isNotEmpty()) mRef.child("name").setValue(cname.text.toString())
            if (caddress.text.isNotEmpty()) mRef.child("address").setValue(caddress.text.toString())
            if (cphone.text.isNotEmpty()) mRef.child("phone").setValue(cphone.text.toString())
            if (cweb.text.isNotEmpty()) mRef.child("website").setValue(cweb.text.toString())
                .addOnSuccessListener {
                    ToolsVisit.vtoast(
                        " تم تعديل بيانات العيادة بنجاح   ",
                        1,
                        context,
                        layoutInflater
                    )
                }
                .addOnFailureListener {
                    ToolsVisit.vtoast(
                        "   لم يتم التعديل خطأ في قاعدة البيانات   ",
                        3,
                        context,
                        layoutInflater
                    )
                }
        }

        fun vtoast(
            text: String,
            type: Int,
            context: android.content.Context,
            layoutInflater: LayoutInflater
        ) {
            val layout = layoutInflater.inflate(R.layout.mtoast, null)
            layout.toasttext.text = text
            when (type) {
                1 -> layout.image.setImageResource(R.drawable.icon_no_frame)
                2 -> layout.image.setImageResource(R.drawable.icon_no_frame)
                3 -> layout.image.setImageResource(R.drawable.icon_no_frame)
            }
            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.setGravity(Gravity.TOP or Gravity.CENTER, 0, 0)
            toast.show()
        }

        fun PickLongDate(
            context: android.content.Context,
            textview: TextView,
            Layoutinflater: LayoutInflater
        ): Long {
            var orginalvalue = textview.text
            val c = Calendar.getInstance()
            var result: Long = 0
            val dpd = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val myCalendar = GregorianCalendar(year, month, dayOfMonth)
                    var dayOfWeek = myCalendar.get(Calendar.DAY_OF_WEEK)
                    if (dayOfWeek > 5) {

                        vtoast(
                            "لا يمكن اختيار يوم عطلة ، يرجى التأكد من التاريخ والمحاولة ثانية!".toString(),
                            3,
                            context,
                            Layoutinflater
                        )
                    } else {
                        var a = myCalendar.timeInMillis.toString()
                        var b = a.substring(0, 10).toLong()
                        result = b
                        textview.text = Tools.epochToStr(b)
                    }
                },
                1,
                2,
                3
            )
            dpd.datePicker.firstDayOfWeek = Calendar.SUNDAY
            dpd.datePicker.minDate = (c.timeInMillis)
            val map = HashMap<String, Booked>()

            for ((timez, time) in map) {
            }
            dpd.show()
            return result
        }

        fun DaysInNumbers(): MutableList<Int> {
            var vacant: MutableList<Int> = mutableListOf()
            var enabledlist: MutableList<String> = mutableListOf()
            var mRef: DatabaseReference? = null
            var mAuth: FirebaseAuth? = null
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            mRef = database.getReference("DaysIn")
            mAuth = FirebaseAuth.getInstance()

            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    enabledlist.clear()
                    vacant.clear()
                    for (n in dataSnapshot.children) {
                        //  val tindex = n.getValue(Long.toString()::class.java)
                        var daynumber: Int = 0
                        if (n.key.toString().contains("1")) {
                            daynumber = 1
                        }
                        if (n.key.toString().contains("2")) {
                            daynumber = 2
                        }
                        if (n.key.toString().contains("3")) {
                            daynumber = 3
                        }
                        if (n.key.toString().contains("4")) {
                            daynumber = 4
                        }
                        if (n.key.toString().contains("5")) {
                            daynumber = 5
                        }
                        if (n.key.toString().contains("6")) {
                            daynumber = 6
                        }
                        if (n.key.toString().contains("7")) {
                            daynumber = 7
                        }
                        if (n.value.toString() == "1") {
                            vacant.add(daynumber)
                        }
                        //                       enabledlist.add(n.value.toString())

                    }


                }
            })
            return vacant
        }

        fun MailtoStr(email: String): String {
            var Str =
                email.toString().replace("@", "").replace(".", "").replace("_", "").replace("-", "")
            return Str
        }
    }


}



