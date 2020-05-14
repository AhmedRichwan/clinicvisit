package com.rashwan.clinicvisit


import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class Tools {

    companion object MyTools {


        fun epochToStr(e: Long): String {

            return LocalDateTime.ofInstant(Instant.ofEpochSecond(e), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd EEEE"))
        }


        fun epochToStrWithoutEEE(e: Long): String {

            return LocalDateTime.ofInstant(Instant.ofEpochSecond(e), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }

        fun strToEpoch(l: String): Long {
            var edited = l
            try {
                if (l.length != 10) {
                    edited = l.substring(0, 10)
                } else edited = l
            } catch (e: Exception) {
                edited = "2020-01-01"
            }

            return LocalDateTime.parse(edited + "T00:00:00.000").atZone(ZoneOffset.systemDefault())
                .toEpochSecond()

        }

        fun strToMilliEpoch(l: Date): Long {
            //   var edited=l.replace(" Sun","")


            return l.time

        }

        fun getDaysDif(fromDate: LocalDate, toDate: LocalDate): Long {
            return ChronoUnit.DAYS.between(fromDate, toDate)
        }

        fun getLocalDateFromString(d: String, format: String): LocalDate {
            return LocalDate.parse(d, DateTimeFormatter.ofPattern(format))
        }

        fun getremain(date: Long): String {
            var mstr = ""
            //remaining
            var diffDays =
                ((date - Tools.strToEpoch(LocalDate.now().toString())) / (86400))
            var daycount = "ايام"
            if (diffDays.toInt() > 11) {
                daycount = "يوما"
            }
            when {
                (diffDays.toInt() == 0) -> mstr = "موعد اليوم!"
                (diffDays.toInt() == 1) -> mstr = "موعد باكر!"
                (diffDays.toInt() == 2) -> mstr = "موعد بعد باكر!"
                (diffDays.toInt() == 3) -> mstr = "متبقي يومان"
                (diffDays.toInt() > 0) -> mstr = "متبقي( $diffDays ) $daycount"
                (diffDays.toInt() < -10) -> {
                    daycount = "يوما"
                    diffDays *= -1
                    mstr = "مضى( " + diffDays.toString() + " )$daycount"
                }
                (diffDays.toInt() == -1) -> mstr = "موعد الأمس"
                (diffDays.toInt() == -2) -> mstr = "مضى يومان"
                (diffDays.toInt() < -2) -> {
                    diffDays = -diffDays
                    mstr = "مضى( " + diffDays.toString() + " )$daycount"
                }
            }
            mstr = "$mstr ⏳"


            return mstr
        }


    }


}




