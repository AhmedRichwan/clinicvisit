package com.rashwan.clinicvisit

import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask


class NetworkTask(var activity: Activity) : AsyncTask<Void, Void, Void>() {

    var dialoge = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
    override fun onPreExecute() {
        val view = activity.layoutInflater.inflate(R.layout.full_screen_progress_bar, null)

        dialoge.setContentView(view)
        dialoge.setCancelable(false)
        dialoge.show()
        super.onPreExecute()
    }

    override fun doInBackground(vararg p0: Void?): Void? {
        Thread.sleep(6000)
        return null
    }

    override fun onPostExecute(result: Void?) {

        super.onPostExecute(result)
        dialoge.dismiss()
    }

    override fun onCancelled() {
        dialoge.dismiss()
        super.onCancelled()
    }

}
