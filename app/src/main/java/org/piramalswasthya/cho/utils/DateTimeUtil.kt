package org.piramalswasthya.cho.utils

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.piramalswasthya.cho.model.AgeUnit
import org.piramalswasthya.cho.ui.register_patient_activity.patient_details.PatientDetailsViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class DateTimeUtil {

    private val nullDate : Date? = null

    val _selectedDate = MutableLiveData(nullDate)

    val selectedDate: MutableLiveData<Date?>
        get() = _selectedDate

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDatePickerDialog(context: Context, initialDate: Date?,) {

        val calendar: Calendar = Calendar.getInstance()
        initialDate?.let {
            calendar.time = it
            _selectedDate.value = it
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context,
            { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // This callback is called when the user selects a date
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                _selectedDate.value = calendar.time
                _selectedDate.value = null

            }, year, month, day)

        datePickerDialog.show()
    }

    companion object {

        const val format = "yyyy-MM-dd HH:mm:ss"

        @RequiresApi(Build.VERSION_CODES.O)
        fun formattedDate(date: Date): String {
            val localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern(format)
            return localDateTime.format(formatter).split(" ")[0]
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun calculateAgeInYears(birthDate: Date): Int {
            val birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val currentDate = LocalDate.now()
            val age = Period.between(birthLocalDate, currentDate)
            return age.years
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun calculateAge(birthDate: Date): Age {
            val birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val currentDate = LocalDate.now()

            val period = Period.between(birthLocalDate, currentDate)

            if(period.years > 0){
                return Age(AgeUnitEnum.YEARS, period.years);
            }
            else if(period.months > 0){
                return Age(AgeUnitEnum.MONTHS, period.months);
            }
            else if(period.days > 7){
                return Age(AgeUnitEnum.WEEKS, period.days/7);
            }
            return Age(AgeUnitEnum.DAYS, period.days);
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun calculateDateOfBirth(value: Int, unit: AgeUnitEnum): Date {
            val days = when(unit){
                AgeUnitEnum.DAYS -> value
                AgeUnitEnum.WEEKS -> value*7
                AgeUnitEnum.MONTHS -> value*30
                AgeUnitEnum.YEARS -> value*365
            }

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -days)
            return calendar.time
        }

        fun formatDateToUTC(date: Date): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(date)
        }

    }

}

data class Age(
    val unit: AgeUnitEnum,
    val value: Int
)

enum class AgeUnitEnum{
    YEARS,
    MONTHS,
    WEEKS,
    DAYS
}

