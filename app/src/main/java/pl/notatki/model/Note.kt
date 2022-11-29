package pl.notatki.model


import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDate.now


private val dateFormat = SimpleDateFormat("MMMM dd, yyyy")
private val timeFormat = SimpleDateFormat("HH:mm")

data class Note(
    val id: Int,
    var title: String,
    var content: String,
    var label: Label?,
    var image: String,
    var reminder: Reminder?,
    var updated_at: LocalDate = now()
) {


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