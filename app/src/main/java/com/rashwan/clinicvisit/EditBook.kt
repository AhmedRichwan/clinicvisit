package com.rashwan.clinicvisit

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.edit_book.*

class EditBook : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_book)
        val textviewid = (R.id.tv)
        val patsexadapter =
            ArrayAdapter(this, R.layout.spstyle, textviewid, arrayOf("غير محدد", "ذكر", "انثى"))
        patsex.adapter = patsexadapter
    }
}
