package pl.notatki.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import pl.notatki.R
import pl.notatki.databinding.ItemMainBinding
import pl.notatki.model.Note
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainAdapter(private val onItemClickListener: (Note) -> Unit) : ListAdapter<Note, MainViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMainBinding.inflate(inflater, parent, false)

        return  MainViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindTo(getItem(position))

    }

    fun setNotes(list: List<Note>){
        super.submitList(list)
    }

    fun setNotesArchive(list: List<Note>){
        val listArchive : MutableList <Note> = mutableListOf()

       /* for (note in list) {
            val reminder = note.reminder
            val reminderTime = reminder?.timeReminder + " " + reminder?.date
            val pattern = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")
            val localDateTime = Date(reminderTime)

            val currentTime: Date = Calendar.getInstance().getTime()

            if(localDateTime>currentTime){
                listArchive.add(note)
            }
        }*/

        super.submitList(listArchive)
    }

    fun setNotesReminders(list: List<Note>){
        val listReminders : MutableList <Note> = mutableListOf()

        for (note in list) {
            val reminder = note.reminder
            val reminderTime = reminder?.timeReminder + " " + reminder?.date

            if (reminder != null) {
                if(reminder.timeReminder != "" || reminder.date != "" || reminder.location != "" ){
                    listReminders.add(note)
                }
            }
        }

        super.submitList(listReminders)
    }

}

class MainViewHolder(private val binding: ItemMainBinding, private val onItemClickListener: (Note) -> Unit) : RecyclerView.ViewHolder(binding.root) {



        fun bindTo(note: Note){
            val reminder = note.reminder
            val timeReminder = reminder?.timeReminder.toString() + "  "
            val dateReminder = reminder?.date.toString() + "  "
            val locationReminder = reminder?.location.toString() + "  "

            binding.cardView.setOnClickListener { onItemClickListener(note) }
            binding.noteTitle.text = note.title
            binding.noteContent.text = note.content
            binding.noteLabel.text = "Etykieta"
            if (reminder != null) {
                if(reminder.timeReminder != "" || reminder.date != "" || reminder.location != "" ) {
                    binding.noteReminder.text = timeReminder + dateReminder + locationReminder
                } else {
                    binding.noteReminder.visibility = View.GONE
                }
            } else {
                binding.noteReminder.visibility = View.GONE
            }

            Glide.with(itemView)
                .load(R.drawable.img)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.noteImg)
        }

}
private val DIFF_CALLBACK: DiffUtil.ItemCallback<Note> = object : DiffUtil.ItemCallback<Note>(){
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.noteId == newItem.noteId
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return  oldItem == newItem
    }

}
