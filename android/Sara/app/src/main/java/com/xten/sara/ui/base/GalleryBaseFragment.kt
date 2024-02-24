package com.xten.sara.ui.base

import android.content.ClipData
import android.content.ClipboardManager
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.example.common.APP_NAME
import com.example.common.MESSAGE_TEXT_COPY
import com.xten.sara.data.model.Gallery
import com.xten.sara.ui.gallery.GalleryItemAdapter

abstract class GalleryBaseFragment<B: ViewDataBinding>(@LayoutRes private val layoutId: Int): BaseFragment<B>(layoutId) {

    protected var albumTypeItemAdapter: GalleryItemAdapter? = null
    protected var listTypeItemAdapter: GalleryItemAdapter? = null

    override fun initGlobalVariables() {
        albumTypeItemAdapter = GalleryItemAdapter(GalleryItemAdapter.TYPE_ALBUM).apply {
            setOnItemClickListener {
                navigateToGalleryDetails(it)
            }
        }
        listTypeItemAdapter = GalleryItemAdapter(GalleryItemAdapter.TYPE_LIST).apply {
            setOnItemClickListener {
                navigateToGalleryDetails(it)
            }
        }
    }

    fun copyToClipboard(clipboardManager: ClipboardManager, text: String) {
        if(text.isNotBlank()) {
            val clipData = ClipData.newPlainText(APP_NAME, text)
            clipboardManager.setPrimaryClip(clipData)
            showToastMessage(MESSAGE_TEXT_COPY)
        }
    }

    abstract fun navigateToGalleryDetails(gallery: Gallery)

    override fun destroyGlobalVariables() {
        super.destroyGlobalVariables()
        albumTypeItemAdapter = null
        listTypeItemAdapter = null
    }

}