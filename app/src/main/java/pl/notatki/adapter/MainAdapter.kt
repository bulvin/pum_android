package pl.notatki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import pl.notatki.R
import pl.notatki.databinding.ItemMainBinding
import pl.notatki.model.Note

class MainAdapter : ListAdapter<Note, MainViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMainBinding.inflate(inflater, parent, false)

        return  MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindTo(getItem(position))

    }



    fun setNotes(list: List<Note>){
        super.submitList(list)
    }

}



class MainViewHolder(private val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(note: Note){
            binding.noteTitle.text = note.title
            binding.noteContent.text = note.content
            binding.noteLabel.text = "Etykieta"
            binding.noteReminder.text = "29 lis 2022, 18:00"

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
