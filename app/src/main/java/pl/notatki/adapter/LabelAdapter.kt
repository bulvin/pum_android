package pl.notatki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.notatki.databinding.ItemLabelBinding
import pl.notatki.model.Label
import pl.notatki.model.Note

class LabelAdapter(private val onItemClickListener: (Label) -> Unit) : ListAdapter<Label, LabelViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLabelBinding.inflate(inflater, parent, false)

        return  LabelViewHolder(binding, onItemClickListener)
    }

    fun setLabels(list: List<Label>){
        super.submitList(list)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

}

class LabelViewHolder(private val binding: ItemLabelBinding, private val onItemClickListener: (Label) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(label: Label){
            binding.labelName.text = label.name
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
