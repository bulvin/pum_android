package pl.notatki.model


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDate.now


private val dateFormat = SimpleDateFormat("MMMM dd, yyyy")
private val timeFormat = SimpleDateFormat("HH:mm")


@Entity
data class Note(
    @PrimaryKey
    val noteId: Int,
    var title: String,
    var content: String,
    var label: Label?,
    var image: String?,
    @Embedded
    var reminder: Reminder?,
    var updated_at: LocalDate = now()
) {


}

@Entity
data class Label(
                @PrimaryKey
                val labelId: Int,
                val name: String,
                val noteLabelId: Int?

) {

}

data class NoteWithLabels(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "noteLabelId"
    )
    val notes: List<Label>?
)


data class Reminder(val id: Int,
                    val date: String = "",
                    val timeReminder: String = "",
                    val location: String = ""

) {
    fun getDate() = dateFormat.parse(date)?.time

    fun getTime() = timeFormat.parse(timeReminder)?.time
}