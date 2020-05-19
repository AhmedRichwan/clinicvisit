package com.rashwan.clinicvisit


class Booked {
    var id: String? = null
    var vnumber: String? = null
    var visitdate: Long? = null
    var sugvisitdate: Long? = null
    var regvisitdate: String? = null
    var modifdate: String? = null
    var time: String? = null
    var sugtime: String? = null
    var isnotified: Int? = null
    var isdeleted: Int? = null
    var ischanged: Int? = null
    var isconfirmed: Int? = null
    var patname: String? = null
    var patemail: String? = null
    var patphone: String? = null
    var age: String? = null
    var sex: Int? = null
    var notes: String? = null
    var paymentvalue: Double? = null
    var paymentref: String? = null
    var user: String? = null


    constructor()


    constructor(
        id: String,
        vnumber: String,
        visitdate: Long,
        sugvisitdate: Long,
        regvisitdate: String,
        modifdate: String,
        time: String,
        sugtime: String,
        isnotified: Int,
        isdeleted: Int,
        ischanged: Int,
        isconfirmed: Int,
        patname: String,
        patemail: String,
        patphone: String,
        age: String,
        sex: Int,
        notes: String,
        paymentvalue: Double,
        paymentref: String,
        user: String
    ) {

        this.id = id
        this.vnumber = vnumber
        this.visitdate = visitdate
        this.sugvisitdate = sugvisitdate
        this.regvisitdate = regvisitdate
        this.modifdate = modifdate
        this.time = time
        this.sugtime = sugtime
        this.isnotified = isnotified
        this.isdeleted = isdeleted
        this.ischanged = ischanged
        this.isconfirmed = isconfirmed
        this.patname = patname
        this.patemail = patemail
        this.patphone = patphone
        this.age = age
        this.sex = sex
        this.notes = notes
        this.paymentvalue = paymentvalue
        this.paymentref = paymentref
        this.user = user
    }

}

class Managers {
    var id: String? = null
    var mgremail: String? = null
    var isenabled: Int? = null


    constructor()


    constructor(
        id: String, mgremail: String, isenabled: Int
    ) {
        this.id = id
        this.mgremail = mgremail
        this.isenabled = isenabled
    }
}

