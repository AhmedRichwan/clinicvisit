package com.rashwan.clinicvisit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.sign_up.*

class SignUp : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.sign_up)
        mAuth = FirebaseAuth.getInstance()
        SUregister.setOnClickListener {
            ToolsVisit.btnanim(it)
            val email = SUemail.text.toString()
            val password = SUpass.text.toString()
            val cpassword = SUcpass.text.toString()
            var phone = SUphone.text.toString()
            val displayname = SUdisplayname.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && password == cpassword) {
                mAuth = FirebaseAuth.getInstance()
                mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            val profileUpdates =
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayname).build()
                            user!!.updateProfile(profileUpdates)
                            user.sendEmailVerification()
                            ToolsVisit.vtoast(
                                "تم التسجيل بنجاح و ارسال رسالة تأكيد الى بريدك الالكتروني ، يجب تأكيد الحساب حتى تتمكن من الحجز",
                                2,
                                this,
                                layoutInflater
                            )
                            startActivity(Intent(this, MainActivity::class.java))
                        } else
                            ToolsVisit.vtoast(
                                it.exception.toString(),
                                2,
                                this,
                                layoutInflater
                            )
                    }
            } else {
                ToolsVisit.vtoast(
                    "سجل البيانات اولا ، الاسم والبريد الالكتروني وكلمة المرور",
                    2,
                    this,
                    layoutInflater
                )
            }
        }
    }
}