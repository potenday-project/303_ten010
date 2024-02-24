package com.xten.sara.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xten.sara.data.model.Gallery
import com.xten.sara.databinding.ItemListMiniBinding
import com.xten.sara.databinding.ItemListWideBinding

class GalleryItemAdapter(private val type: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return when(viewType) {
            0 -> {
                val binding = ItemListMiniBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return AlbumTypeViewHolder(binding)
            }
            else -> {
                val binding = ItemListWideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ListTypeViewHolder(binding)
            }
        }
    }
    override fun getItemCount(): Int = differ.currentList.size
    override fun getItemViewType(position: Int) = type

    private var onItemClick: ((Gallery) -> Unit)? = null
    fun setOnItemClickListener(onItemClick: (Gallery) -> Unit) {
        this.onItemClick = onItemClick
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = differ.currentList[position]
        when(holder) {
            is AlbumTypeViewHolder -> holder.binding.apply {
                gallery = item
                view.setOnClickListener {
                    onItemClick?.let { it(item) }
                }
            }
            is ListTypeViewHolder -> holder.binding.apply {
                gallery = item
                view.setOnClickListener {
                    onItemClick?.let { it(item) }
                }
            }
        }
    }

    inner class AlbumTypeViewHolder(val binding: ItemListMiniBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ListTypeViewHolder(val binding: ItemListWideBinding) : RecyclerView.ViewHolder(binding.root)

    private val differ = AsyncListDiffer(this, DIFF_UTIL)
    fun submitData(list: List<Gallery>) = differ.submitList(list)

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Gallery>() {
            override fun areItemsTheSame(oldItem: Gallery, newItem: Gallery)= oldItem._id == newItem._id
            override fun areContentsTheSame(oldItem: Gallery, newItem: Gallery) = oldItem == newItem
        }
        const val TYPE_ALBUM = 0
        const val TYPE_LIST = 1
        const val GRID_COL_TYPE_1 = 2
    }
}