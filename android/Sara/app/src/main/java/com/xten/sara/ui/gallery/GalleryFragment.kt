package com.xten.sara.ui.gallery

import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.*
import com.example.common.MESSAGE_RESULT_SEARCH_FAIL
import com.example.common.MESSAGE_WARNING_EDIT
import com.xten.sara.R
import com.xten.sara.data.model.Gallery
import com.xten.sara.databinding.FragmentGalleryBinding
import com.xten.sara.extensions.dropDownSoftKeyboard
import com.xten.sara.extensions.scrollTop
import com.xten.sara.extensions.setEnterKeyEvent
import com.xten.sara.extensions.setViewType
import com.xten.sara.ui.base.BaseFragment
import com.xten.sara.ui.base.GalleryBaseFragment
import com.xten.sara.ui.gallery.GalleryItemAdapter.Companion.TYPE_ALBUM
import com.xten.sara.ui.gallery.GalleryItemAdapter.Companion.TYPE_LIST
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment : GalleryBaseFragment<FragmentGalleryBinding>(R.layout.fragment_gallery) {

    private val galleryViewModel: GalleryViewModel by viewModels()

    override fun setupBinding(binding: FragmentGalleryBinding): FragmentGalleryBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@GalleryFragment
            viewModel = galleryViewModel
        }
    }

    override fun setData() {
        galleryViewModel.updateGallery()
    }

    @Inject
    lateinit var inputManager: InputMethodManager
    override fun initView() = binding.run {
        recyclerView.setViewType(
            context = requireContext(),
            type = TYPE_ALBUM,
            albumTypeItemAdapter = albumTypeItemAdapter!!,
            listTypeItemAdapter = listTypeItemAdapter!!
        )
        editSearch.setEnterKeyEvent {
            onEnterKeyPressed()
        }
    }

    fun changeType(isChecked: Boolean) {
        val type = if(isChecked) TYPE_ALBUM else TYPE_LIST
        binding.recyclerView.setViewType(
            context = requireContext(),
            type = type,
            albumTypeItemAdapter = albumTypeItemAdapter!!,
            listTypeItemAdapter = listTypeItemAdapter!!
        )
    }

    private var param = ""
    private fun onEnterKeyPressed() {
        inputManager.dropDownSoftKeyboard(requireActivity())
        param = binding.editSearch.text.toString().trim()
        when {
            param.isNotBlank() -> {
                binding.recyclerView.scrollTop(isSmooth = false)
                galleryViewModel.requestSearch(param)
            }
            else -> showToastMessage(MESSAGE_WARNING_EDIT)
        }
    }

    fun resetData() = binding.run {
        inputManager.dropDownSoftKeyboard(requireActivity())
        editSearch.text?.clear()
        galleryViewModel.resetGallery()
        recyclerView.scrollTop(isSmooth = true)
    }

    override fun subscribeToObservers() {
        galleryViewModel.galleryList.observe(viewLifecycleOwner) {
            handleGalleryListData(it)
        }
    }

    private fun handleGalleryListData(data: List<Gallery>?) = data?.let {
        binding.recyclerView.scrollTop(!saved)
        if(it.isEmpty() && param.isNotBlank()) showToastMessage(MESSAGE_RESULT_SEARCH_FAIL)

        val submitList = it.sortedByDescending { image -> image.createdAt }
        albumTypeItemAdapter?.submitData(submitList)
        listTypeItemAdapter?.submitData(submitList)
    }


    private var saved = false
    override fun navigateToGalleryDetails(gallery: Gallery) {
        saved = true
        val action = GalleryFragmentDirections.actionGalleryFragmentToGalleryDetailsFragment(gallery)
        navigateToDirections(action)
    }

    override fun onStop() {
        if(!saved) {
            binding.editSearch.text?.clear()
            binding.btnAlbum.isChecked = true
        }
        super.onStop()
    }

}