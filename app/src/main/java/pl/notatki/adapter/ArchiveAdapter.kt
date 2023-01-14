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
import java.util.*

class ArchiveAdapter(private val onItemClickListener: (Note) -> Unit) : ListAdapter<Note, ArchiveViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMainBinding.inflate(inflater, parent, false)

        return  ArchiveViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ArchiveViewHolder, position: Int) {
        holder.bindTo(getItem(position))

    }

    fun setNotes(list: List<Note>){
        super.submitList(list)
    }

}

class ArchiveViewHolder(private val binding: ItemMainBinding, private val onItemClickListener: (Note) -> Unit) : RecyclerView.ViewHolder(binding.root) {


        fun bindTo(note: Note){
            val reminder = note.reminder
            val timeReminder = reminder?.timeReminder.toString() + "  "
            val dateReminder = reminder?.date.toString() + "  "
            val locationReminder = reminder?.location.toString() + "  "

            binding.cardView.setOnClickListener { onItemClickListener(note) }
            binding.noteTitle.text = note.title
            binding.noteContent.text = note.content
            binding.noteLabel.text = "Etykieta"
            if(reminder!=null) {
                binding.noteReminder.text = timeReminder + dateReminder + locationReminder
            } else {
                binding.noteReminder.visibility = View.GONE
            }

            if (note.image != ""){
                Glide.with(itemView)
                    .load(R.drawable.img)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.noteImg)
            } else {
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
