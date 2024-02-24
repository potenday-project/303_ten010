package com.xten.sara.ui.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.xten.sara.extensions.putExtraCameraUri
import com.xten.sara.util.ImageFileUtils
import java.io.File

abstract class ImagePickupBaseFragment<B: ViewDataBinding>(@LayoutRes private val layoutId: Int): BaseFragment<B>(layoutId) {

    private var cacheFile: File? = null

    protected fun startGalleryChooserIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            cacheFile = putExtraCameraUri(requireContext()) { uri ->
                updateImageUri(uri)
            }
        }
        val chooserIntent = ImageFileUtils.createChooserIntent(cameraIntent)
        chooserIntentLauncher.launch(chooserIntent)
    }

    private val chooserIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == Activity.RESULT_OK) handleImagePickResultOk(it.data)
        cacheFile?.delete()
    }

    abstract fun updateImageUri(uri: Uri?)


    // data가 null일 때 -> 카메라 픽업
    protected open fun handleImagePickResultOk(intent: Intent?) = intent?.data?.also {
        updateImageUri(it)
    }

}