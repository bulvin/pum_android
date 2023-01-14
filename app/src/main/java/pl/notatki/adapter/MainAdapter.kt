package pl.notatki.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import pl.notatki.R
import pl.notatki.databinding.ItemMainBinding
import pl.notatki.model.Note

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

}



class MainViewHolder(private val binding: ItemMainBinding, private val onItemClickListener: (Note) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(note: Note){
            binding.cardView.setOnClickListener { onItemClickListener(note) }
            binding.noteTitle.text = note.title
            binding.noteContent.text = note.content
            binding.noteLabel.text = "Etykieta"
            binding.noteReminder.text = "29 lis 2022, 18:00"


            if (note.image != ""){
                Glide.with(itemView)
                    .load(note.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.noteImg)
            }
            else{
                binding.noteImg.visibility = View.GONE
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
