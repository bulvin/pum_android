package pl.notatki.model


import androidx.annotation.Nullable
import androidx.room.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDate.now


private val dateFormat = SimpleDateFormat("MMMM dd, yyyy")
private val timeFormat = SimpleDateFormat("HH:mm")

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var noteId: Int?,
    var title: String,
    var content: String,
    var image: String?,
    @Embedded
    var reminder: Reminder?,
    var updated_at: String?
){

}

@Entity
data class Label(
                @PrimaryKey
                var labelId: Int,
                var name: String,

)


@Entity(primaryKeys = ["noteId", "labelId"])
data class NoteLabelCrossRef(
    var noteId: Int,
    var labelId: Int
)

data class NoteWithLabels(
    @Embedded var note: Note,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "labelId",
        associateBy = Junction(NoteLabelCrossRef::class)
    )
    var labels: List<Label>?
)


data class Reminder(
                    var date: String = "",
                    var timeReminder: String = "",
                    var location: String = ""

) {
//  fun getDate() = dateFormat.parse(date)?.time
//
//   fun getTime() = timeFormat.parse(timeReminder)?.time
}