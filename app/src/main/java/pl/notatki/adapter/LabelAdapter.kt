package pl.notatki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.notatki.databinding.ItemLabelBinding
import pl.notatki.model.Label

class LabelAdapter : ListAdapter<Label, LabelViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLabelBinding.inflate(inflater, parent, false)

        return  LabelViewHolder(binding)
    }

    fun setLabels(list: List<Label>){
        super.submitList(list)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

}


class LabelViewHolder(private val binding: ItemLabelBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(label: Label){
            binding.editLabel
            binding.deleteLabelButton
            binding.checkBox
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
