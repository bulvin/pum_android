package pl.notatki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.notatki.databinding.ItemLabelBinding
import pl.notatki.databinding.ItemNoteLabelBinding
import pl.notatki.model.Label
import pl.notatki.model.Note

class NoteLabelAdapter(private val onItemClickListener: (Label) -> Unit) : ListAdapter<Label,  NoteLabelViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteLabelViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNoteLabelBinding.inflate(inflater, parent, false)

        return  NoteLabelViewHolder(binding, onItemClickListener)
    }

    fun setLabels(list: List<Label>){
        super.submitList(list)
    }

    override fun onBindViewHolder(holder: NoteLabelViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

}

class NoteLabelViewHolder(private val binding: ItemNoteLabelBinding, private val onItemClickListener: (Label) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(label: Label){
            binding.labelName.text = label.name
            binding.checkBox
            binding.cardView.setOnClickListener({onItemClickListener(label)})
        }
}



private val DIFF_CALLBACK: DiffUtil.ItemCallback<Label> = object : DiffUtil.ItemCallback<Label>(){

    override fun areItemsTheSame(oldItem: Label, newItem: Label): Boolean {
        return oldItem.labelId == newItem.labelId
    }

    override fun areContentsTheSame(oldItem: Label, newItem: Label): Boolean {
        return  oldItem == newItem
    }

}
