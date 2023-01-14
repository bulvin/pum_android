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


    private var originalList: List<Note> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMainBinding.inflate(inflater, parent, false)

        return  MainViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindTo(getItem(position))

    }

    fun setNotes(list: List<Note>){
        val listNotes : MutableList <Note> = mutableListOf()

        for (note in list) {

            if(!note.archived){
                listNotes.add(note)

            }

        }
        originalList = listNotes
        super.submitList(originalList)
    }

    fun setNotesSearch(list: List<Note>, searched: String){ //Do wyszukiwania w notatkach, sprawdza tytuł i treść notatki czy zawiera wyszukiwane słowo
        val listSearched : MutableList <Note> = mutableListOf()

        for (note in list) {    //Najpierw tytuł
            val title = note.title

            if (title.contains(searched, true) == true) {
                listSearched.add(note)
            }
        }

        for (note in list) {    //Potem notatka
            val description = note.content

            if (description.contains(searched, true) == true) {
                listSearched.add(note)
            }
        }

        super.submitList(listSearched)
    }

    fun setNotesArchive(list: List<Note>){
        val listArchive : MutableList <Note> = mutableListOf()

        for (note in list) {

            if(note.archived == true ){
                listArchive.add(note)
            }

        }

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
    fun setData(list: List<Note>){
        originalList = list
        super.submitList(list)
    }
    fun filter(newText: String) {
            submitList(originalList.filter { note -> note.title.contains(newText, ignoreCase = true) })
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


            if (note.image != null){
                Glide.with(itemView)
                    .load(note.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.noteImg)
            }
            if (note.image == ""){
                binding.noteImg.visibility = View.GONE;
            }

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
