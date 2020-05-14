package com.rashwan.clinicvisit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.allreservation.*
import kotlinx.android.synthetic.main.rowstyle.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList

class MyReservation : AppCompatActivity() {
    var mNotelist: ArrayList<Booked>? = null
    var mAuth: FirebaseAuth? = null
    var mRef: DatabaseReference? = null
    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val todayinsec = Calendar.getInstance().timeInMillis.toString()
        .substring(0, Calendar.getInstance().timeInMillis.toString().length - 3).toLong() - 86400

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_reservation)
        mRef = database.getReference("Booked")
        mAuth = FirebaseAuth.getInstance()
        mNotelist = ArrayList()
        listViewControl(allbtn)
        btnswitch.setOnCheckedChangeListener { _, isChecked ->
            var hint = ""

            if (isChecked == true) {
                searchtext.isEnabled = true
                hint = " نتيجة البحث عن" + searchtext.text


            } else {
                hint = ""
                searchtext.isEnabled = false
            }

        }
        res_LV.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val sortedList = mNotelist?.sortedWith(compareBy { it.visitdate })?.toList()
                val vBook = sortedList?.get(position)!!
                val BdCard = Intent(this, BookDetails::class.java)

                BdCard.putExtra("id", vBook.id)
                BdCard.putExtra("patname", vBook.patname)
                BdCard.putExtra("vnumber", vBook.vnumber.toString())
                BdCard.putExtra("patphone", vBook.patphone)
                BdCard.putExtra("patemail", vBook.patemail)
                BdCard.putExtra("patage", vBook.age.toString())
                BdCard.putExtra("visitdate", Tools.epochToStr(vBook.visitdate!!.toLong()))
                BdCard.putExtra("time", vBook.time)
                BdCard.putExtra("sex", vBook.sex)
                BdCard.putExtra("isconfirmed", vBook.isconfirmed)
                BdCard.putExtra("remain", remain.text.toString())
                BdCard.putExtra("creator", vBook.user)


                startActivity(BdCard)
            }
/*

        res_LV.onItemLongClickListener=
            AdapterView.OnItemLongClickListener { _, _, p2, _ ->
                val bookdbid = mNotelist?.get(p2)!!
                val alertBuilder = AlertDialog.Builder(this)
                var update_book_view = layoutInflater.inflate(R.layout.book_operations, null)
                val alertDialog = alertBuilder.create()
                alertDialog.setView(update_book_view)
                alertDialog.show()
                update_book_view.confirm.setOnClickListener {
                    var childRef = mRef?.child(bookdbid.id!!)
                    childRef?.child("isbooked")?.setValue(1)
                    Toast.makeText(this, "تم التعديل", Toast.LENGTH_LONG).show()
                    alertDialog.dismiss()
                }


                true
            }

*/


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun listViewControl(view: View) {
        mNotelist?.clear()
        var bookstate = 99
        val today = LocalDate.now()
        var firstdayofweek = today
        while (firstdayofweek.dayOfWeek !== DayOfWeek.SATURDAY) {
            firstdayofweek = firstdayofweek.minusDays(1)
        }
        var lastdayofweek = today
        while (lastdayofweek.dayOfWeek !== DayOfWeek.FRIDAY) {
            lastdayofweek = lastdayofweek.plusDays(1)
        }
        var firstdayofmonth = today
        while (firstdayofmonth.dayOfMonth != 1) {
            firstdayofmonth = firstdayofmonth.minusDays(1)
        }
        val lastdayofmonth = firstdayofmonth.with(TemporalAdjusters.lastDayOfMonth())
        val lastdayofnextmonth =
            firstdayofmonth.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth())
        val firstDayOfWeek = Tools.strToEpoch(firstdayofweek.toString())
        val lastDayOfWeek = Tools.strToEpoch(lastdayofweek.toString())
        val startmonth = Tools.strToEpoch(firstdayofmonth.toString())
        val endmonth = Tools.strToEpoch(lastdayofmonth.toString())
        val nextmonthstart = Tools.strToEpoch(lastdayofmonth.toString()) + 86400
        val nextmonthend = Tools.strToEpoch(lastdayofnextmonth.toString())
        val currentlogged = mAuth?.currentUser?.email.toString()

//        var delStat=false
        when (view.id) {
            R.id.prndingbtn -> bookstate = 0
            R.id.allconfirmed -> bookstate = 1
            R.id.completedbtn -> bookstate = 2
//     R.id.missedbtn->bookstate=3
            R.id.cancelledbtn -> bookstate = 4
            R.id.changerequestbtn -> bookstate = 5
            R.id.cancelrequestsbtn -> bookstate = 6
//     R.id.allrequests->bookstate=15
//    R.id.confirmedthisweek->bookstate=1
//    R.id.confirmednextweek->bookstate=1

        }
        /*
        when {
            view.id == (R.id.allbtn) -> {
                firstDayOfWeek = "0".toLong()
                lastDayOfWeek = "999999999999".toLong()

            }
            view.id == (R.id.cwbtn) -> {

                firstDayOfWeek = tools.strToEpoch(firstdayofweek.toString())
                lastDayOfWeek = tools.strToEpoch(lastdayofweek.toString())

            }
            view.id == (R.id.nwbtn) -> {
                firstDayOfWeek = tools.strToEpoch(firstdayofweek.toString())+ 604800
                lastDayOfWeek = tools.strToEpoch(lastdayofweek.toString())+ 604800

            }
            view.id == (R.id.anwbtn) -> {
                firstDayOfWeek = tools.strToEpoch(firstdayofweek.toString()) + 1209600
                lastDayOfWeek = tools.strToEpoch(lastdayofweek.toString()) + 1209600

            }
            view.id == (R.id.cm) -> {
                firstDayOfWeek = tools.strToEpoch(firstdayofmonth.toString())
                lastDayOfWeek = tools.strToEpoch(lastdayofmonth.toString())

            }


            view.id == (R.id.nm) -> {
                firstDayOfWeek = tools.strToEpoch(lastdayofmonth.toString())+86400
                lastDayOfWeek = tools.strToEpoch(lastdayofnextmonth.toString())

            }
            view.id == R.id.delbtn -> {
                firstDayOfWeek = "0".toLong()
                lastDayOfWeek = "999999999999".toLong()
                delStat=true
            }
        }*/
        mRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                progressBar.visibility = View.VISIBLE
                mNotelist?.clear()

                try {
                    for (n in p0.children) {
                        val book = n.getValue(Booked::class.java)!!

                        if (view.id == R.id.allbtn && book.user == currentlogged) mNotelist!!.add(
                            0,
                            book
                        )
                        if (book.user == currentlogged) {
                            val searchcontext =
                                book.patname.toString() + book.patphone.toString() + book.patname.toString()
                            if (btnswitch.isChecked && searchtext.text.isEmpty()) btnswitch.isChecked =
                                false
                            if (btnswitch.isChecked && searchcontext.contains(searchtext.text)) {

                                if (book.isconfirmed == bookstate && book.visitdate!! > todayinsec && book.isdeleted == 0) mNotelist!!.add(
                                    0,
                                    book
                                )
                                if (view.id == R.id.allrequests && book.isconfirmed.toString() in ("56") && book.visitdate!! > todayinsec && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.missedbtn && book.isconfirmed != 2 && book.visitdate!! < todayinsec && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.completedbtn && book.visitdate!! < todayinsec && book.isconfirmed == 2 && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.confirmedthisweek && book.visitdate!! <= lastDayOfWeek && book.visitdate!! >= firstDayOfWeek && book.isconfirmed == 1 && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.confirmednextweek && book.visitdate!! <= lastDayOfWeek + 604800 && book.visitdate!! >= firstDayOfWeek + 604800 && book.isconfirmed == 1 && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                //monthly confirmed
                                if (view.id == R.id.confirmedthismonth && book.visitdate!! <= endmonth && book.visitdate!! >= startmonth && book.isconfirmed == 1 && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.confirmednextmonth && book.visitdate!! <= nextmonthend && book.visitdate!! >= nextmonthstart && book.isconfirmed == 1 && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.deletedbooks && book.isdeleted == 1) {
                                    mNotelist!!.add(0, book)
                                }


                            }
                            if (!btnswitch.isChecked) {
                                if (book.isconfirmed == bookstate && book.visitdate!! > todayinsec && book.isdeleted == 0) mNotelist!!.add(
                                    0,
                                    book
                                )
                                if (view.id == R.id.allrequests && book.isconfirmed.toString() in ("56") && book.visitdate!! > todayinsec && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.missedbtn && book.isconfirmed != 2 && book.visitdate!! < todayinsec && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.completedbtn && book.visitdate!! < todayinsec && book.isconfirmed == 2 && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.confirmedthisweek && book.visitdate!! <= lastDayOfWeek && book.visitdate!! >= firstDayOfWeek && book.isconfirmed == 1 && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.confirmednextweek && book.visitdate!! <= lastDayOfWeek + 604800 && book.visitdate!! >= firstDayOfWeek + 604800 && book.isconfirmed == 1 && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                //monthly confirmed
                                if (view.id == R.id.confirmedthismonth && book.visitdate!! <= endmonth && book.visitdate!! >= startmonth && book.isconfirmed == 1 && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.confirmednextmonth && book.visitdate!! <= nextmonthend && book.visitdate!! >= nextmonthstart && book.isconfirmed == 1 && book.isdeleted == 0) {
                                    mNotelist!!.add(0, book)
                                }
                                if (view.id == R.id.deletedbooks && book.isdeleted == 1) {
                                    mNotelist!!.add(0, book)
                                }


                            }
                        }
                    }


                    val sortedList =
                        mNotelist?.sortedWith(compareBy({ it.visitdate }))?.toList()
                    val noteadapter = visitadapter(application, sortedList!!)
                    res_LV.adapter = noteadapter
                    if (mNotelist?.count() == 0) {
                        if (btnswitch.isChecked && searchtext.text.isNotEmpty()) {
//                        Pbar.isVisible = false

                            TTitle.text =
                                " لا يوجد نتائج للبحث عن ' " + searchtext.text + "  ' في " + "" + view.contentDescription


                        } else {
//                        Pbar.isVisible = false
                            TTitle.text = " عفوا لا يوجد حجوزات في " + view.contentDescription
                        }
                    } else {
                        val casecount = " ( " + mNotelist?.count().toString() + " )"
                        if (btnswitch.isChecked) {
                            var hint =
                                " نتيجة البحث عن ' " + searchtext.text + "  ' في " + "" + view.contentDescription + casecount
                            TTitle.text = hint
                        } else {
                            TTitle.text = "" + view.contentDescription + casecount
                        }
                    }
                    progressBar.visibility = View.GONE

                } catch (e: Exception) {
                    Toast.makeText(this@MyReservation, e.message.toString(), Toast.LENGTH_LONG)
                        .show()
                    progressBar.visibility = View.GONE
                }

            }
        })


    }

}
