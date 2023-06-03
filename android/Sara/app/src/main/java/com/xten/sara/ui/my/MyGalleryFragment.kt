package com.xten.sara.ui.my

import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.*
import com.xten.sara.R
import com.xten.sara.data.Gallery
import com.xten.sara.databinding.FragmentMyGalleryBinding
import com.xten.sara.ui.base.BaseFragment
import com.xten.sara.ui.gallery.GalleryItemAdapter
import com.xten.sara.ui.gallery.GalleryViewModel
import com.xten.sara.util.constants.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MyGalleryFragment : BaseFragment<FragmentMyGalleryBinding>(R.layout.fragment_my_gallery) {

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

    private var albumTypeItemAdapter: GalleryItemAdapter? = null
    private var listTypeItemAdapter: GalleryItemAdapter? = null
    override fun initGlobalVariables() {
        albumTypeItemAdapter = GalleryItemAdapter(TYPE_ALBUM).apply {
            addOnItemClickListener()
        }
        listTypeItemAdapter = GalleryItemAdapter(TYPE_LIST).apply {
            addOnItemClickListener()
        }
    }

    override fun GalleryItemAdapter.addOnItemClickListener() {
        setOnItemClickListener { navigateToGalleryDetails(it) }
    }

    @Inject lateinit var inputManager: InputMethodManager
    override fun initView() = binding.run {
        recyclerView.setViewType(TYPE_ALBUM)
        editSearch.setup(inputManager)
    }

    private fun RecyclerView.setViewType(type: Int) = when(type) {
        TYPE_ALBUM -> {
            val gridLayoutManager = GridLayoutManager(requireContext(), GRID_COL_TYPE_1)
            layoutManager = gridLayoutManager
            adapter = albumTypeItemAdapter
        }
        else -> {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager = linearLayoutManager
            adapter = listTypeItemAdapter
        }
    }

    fun changeType(isChecked: Boolean) = binding.recyclerView.setViewType(if(isChecked) TYPE_ALBUM else TYPE_LIST)

    private var param = ""
    override fun handleEnterKeyEvent(inputManager: InputMethodManager): Boolean {
        param = binding.editSearch.text.toString().trim()
        verifyInputState(inputManager, param.isBlank(), param)
        return super.handleEnterKeyEvent(inputManager)
    }

    override fun hasVerifiedInputState(param: String) {
        galleryViewModel.requestSearch(param)
        binding.recyclerView.scrollToPosition(DEFAULT_POSITION)
    }

    fun resetData() = binding.run {
        editSearch.text?.clear()
        galleryViewModel.resetGallery()
        recyclerView.smoothScrollToPosition(DEFAULT_POSITION)
        dropDownSoftKeyboard(inputManager)
    }

    override fun subscribeToObservers() {
        galleryViewModel.galleryList.observe(viewLifecycleOwner) {
            handleGalleryListData(it)
        }
    }

    private fun handleGalleryListData(data: List<Gallery>?) = data?.let {
        if(it.isEmpty() && param.isNotBlank()) showToastMessage(MESSAGE_RESULT_SEARCH_FAIL)

        val submitList = it.sortedByDescending { image -> image.createdAt }
        albumTypeItemAdapter?.submitData(submitList)
        listTypeItemAdapter?.submitData(submitList)
    }

    private fun navigateToGalleryDetails(gallery: Gallery) {
        val action = MyGalleryFragmentDirections.actionMyGalleryFragmentToGalleryDetailsFragment(gallery)
        navigateToDirections(action)
    }

    override fun destroyGlobalVariables() {
        super.destroyGlobalVariables()
        albumTypeItemAdapter = null
        listTypeItemAdapter = null
    }

}