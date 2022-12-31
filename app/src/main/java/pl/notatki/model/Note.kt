package pl.notatki.model


import android.os.Parcelable
import androidx.annotation.Nullable
import androidx.room.*
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDate.now


private val dateFormat = SimpleDateFormat("MMMM dd, yyyy")
private val timeFormat = SimpleDateFormat("HH:mm")

@Entity
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    var noteId: Int?,
    var title: String,
    var content: String,
    var image: String?,
    @Embedded
    var reminder: Reminder?,
    var updated_at: String?,

) : Parcelable{

}

@Entity
@Parcelize
data class Label(
                @PrimaryKey
                var labelId: Int?,
                var name: String,

) : Parcelable{

}


@Entity(primaryKeys = ["noteId", "labelId"])
@Parcelize
data class NoteLabelCrossRef(
    var noteId: Int,
    var labelId: Int
) : Parcelable


@Parcelize
data class NoteWithLabels(
    @Embedded var note: Note,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "labelId",
        associateBy = Junction(NoteLabelCrossRef::class)
    )
    var labels: List<Label>?
) : Parcelable

@Parcelize
data class Reminder(
                    var date: String = "",
                    var timeReminder: String = "",
                    var location: String = ""

) : Parcelable {
//  fun getDate() = dateFormat.parse(date)?.time
//
//   fun getTime() = timeFormat.parse(timeReminder)?.time
}