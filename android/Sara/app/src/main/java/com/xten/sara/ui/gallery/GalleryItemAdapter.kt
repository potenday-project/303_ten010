package com.xten.sara.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xten.sara.data.Gallery
import com.xten.sara.databinding.ItemListMiniBinding

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-30
 * @desc
 */
class GalleryItemAdapter: RecyclerView.Adapter<GalleryItemAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemListMiniBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListMiniBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    private val differ = AsyncListDiffer(this, DIFF_UTIL)
    override fun getItemCount(): Int = differ.currentList.size

    fun submitData(list: List<Gallery>) {
        differ.submitList(list)
    }

    private var onItemClick: ((Gallery) -> Unit)? = null
    fun setOnItemClickListener(onItemClick: (Gallery) -> Unit) {
        this.onItemClick = onItemClick
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            gallery = item
            view.setOnClickListener {
                onItemClick?.let { it(item) }
            }
        }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Gallery>() {
            override fun areItemsTheSame(oldItem: Gallery, newItem: Gallery)= oldItem._id == newItem._id
            override fun areContentsTheSame(oldItem: Gallery, newItem: Gallery) = oldItem == newItem
        }
    }
}