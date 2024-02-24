package com.xten.sara.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.DEFAULT_POSITION
import com.xten.sara.ui.gallery.GalleryItemAdapter
import com.xten.sara.ui.gallery.GalleryItemAdapter.Companion.GRID_COL_TYPE_1
import com.xten.sara.ui.gallery.GalleryItemAdapter.Companion.TYPE_ALBUM

fun RecyclerView.setViewType(
    context: Context,
    type: Int,
    albumTypeItemAdapter: GalleryItemAdapter,
    listTypeItemAdapter: GalleryItemAdapter
) = when(type) {
    TYPE_ALBUM -> {
        val gridLayoutManager = GridLayoutManager(context, GRID_COL_TYPE_1)
        layoutManager = gridLayoutManager
        adapter = albumTypeItemAdapter
    }
    else -> {
        val linearLayoutManager = LinearLayoutManager(context)
        layoutManager = linearLayoutManager
        adapter = listTypeItemAdapter
    }
}

fun RecyclerView.scrollTop(isSmooth: Boolean, DEFAULT_POSITION: Int = 0) = when {
    isSmooth -> smoothScrollToPosition(DEFAULT_POSITION)
    else -> scrollToPosition(DEFAULT_POSITION)
}