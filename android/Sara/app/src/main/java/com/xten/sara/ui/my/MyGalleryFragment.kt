package com.xten.sara.ui.my

import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.common.MESSAGE_WARNING_EDIT
import com.xten.sara.R
import com.xten.sara.data.model.Gallery
import com.xten.sara.databinding.FragmentMyGalleryBinding
import com.xten.sara.extensions.dropDownSoftKeyboard
import com.xten.sara.extensions.scrollTop
import com.xten.sara.extensions.setEnterKeyEvent
import com.xten.sara.extensions.setViewType
import com.xten.sara.ui.base.GalleryBaseFragment
import com.xten.sara.ui.gallery.GalleryItemAdapter.Companion.TYPE_ALBUM
import com.xten.sara.ui.gallery.GalleryItemAdapter.Companion.TYPE_LIST
import com.xten.sara.ui.gallery.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MyGalleryFragment : GalleryBaseFragment<FragmentMyGalleryBinding>(R.layout.fragment_my_gallery) {

    private val galleryViewModel: GalleryViewModel by viewModels()
    private val args: MyGalleryFragmentArgs by navArgs()

    override fun setupBinding(binding: FragmentMyGalleryBinding): FragmentMyGalleryBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = galleryViewModel
        }
    }

    override fun setData() {
        galleryViewModel.updateGallery(args.email)
    }

    @Inject lateinit var inputManager: InputMethodManager
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
        param = binding.editSearch.text.toString().trim()
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
        if(it.isEmpty() && param.isNotBlank()) showToastMessage(com.example.common.MESSAGE_RESULT_SEARCH_FAIL)

        val submitList = it.sortedByDescending { image -> image.createdAt }
        albumTypeItemAdapter?.submitData(submitList)
        listTypeItemAdapter?.submitData(submitList)
    }

    override fun navigateToGalleryDetails(gallery: Gallery) {
        val action = MyGalleryFragmentDirections.actionMyGalleryFragmentToGalleryDetailsFragment(gallery)
        navigateToDirections(action)
    }

}