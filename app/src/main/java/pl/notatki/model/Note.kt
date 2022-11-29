package pl.notatki.model


import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDate.now



private val dateFormat = SimpleDateFormat("MMMM dd, yyyy")
private val timeFormat = SimpleDateFormat("HH:mm")

data class Note(val id: Int,
                val title: String,
                val content: String,
                val label: Label,
                val image: String,
                val reminder: Reminder,
                val updated_at: LocalDate = now()
) {

    fun getEditDate(){
        dateFormat.format(updated_at)
    }

}


data class Label(val id: Int,
                 val name: String

) {

}


data class Reminder(val id: Int,
                    val date: String = "",
                    val timeReminder: String = "",
                    val location: String = ""

) {
    fun getDate() = dateFormat.parse(date)?.time

    fun getTime() = timeFormat.parse(timeReminder)?.time
}