package com.xten.sara.ui.gallery

import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.dropDownSoftKeyboard
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.data.Gallery
import com.xten.sara.databinding.FragmentGalleryBinding
import com.xten.sara.util.constants.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private val galleryViewModel: GalleryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestUpdateGallery()
        return getBinding(container).root
    }
    private fun requestUpdateGallery() {
        galleryViewModel.updateGallery()
    }
    private fun getBinding(container: ViewGroup?) : FragmentGalleryBinding {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_gallery, container, false)
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = galleryViewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        subscribeToObservers()
    }

    private fun initView() = binding.apply {
        initRecyclerView()
        initTypeChangeButtons()
        initSearchEditText()
        initResetButton()
    }

    private fun initRecyclerView() {
        setRecyclerViewItemType(TYPE_ALBUM)
    }
    private fun setRecyclerViewItemType(type: Int) = binding.recyclerView.apply {
        when(type) {
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
    }
    private val albumTypeItemAdapter by lazy {
        GalleryItemAdapter(TYPE_ALBUM).apply {
            setOnItemClickListener {
                navigateToGalleryDetails(it)
            }
        }
    }
    private val listTypeItemAdapter by lazy {
        GalleryItemAdapter(TYPE_LIST).apply {
            setOnItemClickListener {
                navigateToGalleryDetails(it)
            }
        }
    }

    private fun initTypeChangeButtons() = binding.apply {
        btnAlbum.setOnCheckedChangeListener { _, isChecked ->
            setTypeButtonAction(isChecked)
        }
    }
    private fun setTypeButtonAction(isChecked: Boolean) {
        when {
            isChecked -> setRecyclerViewItemType(TYPE_ALBUM)
            else -> setRecyclerViewItemType(TYPE_LIST)
        }
    }

    private fun initSearchEditText() = binding.editSearch.apply {
        editableText.clear()
        setOnKeyListener { _, keyCode, _ ->
            when(keyCode) {
                KeyEvent.KEYCODE_ENTER -> handleEnterKeyEvent()
                else -> return@setOnKeyListener false
            }
        }
    }
    @Inject
    lateinit var inputManager: InputMethodManager
    private fun handleEnterKeyEvent(): Boolean {
        dropDownSoftKeyboard(requireActivity(), inputManager)
        requestSearch()

        return true
    }

    private var param = ""
    private fun requestSearch() {
        param = binding.editSearch.text.toString().trim()
        if(param.isBlank()) {
            showToast(requireContext(), MESSAGE_WARNING_EDIT)
            return
        }
        galleryViewModel.requestSearch(param)
        recyclerViewSmoothTopScroll(false)
    }

    private fun recyclerViewSmoothTopScroll(isSmooth: Boolean) = binding.recyclerView.apply {
        when {
            isSmooth -> smoothScrollToPosition(DEFAULT_POSITION)
            else -> scrollToPosition(DEFAULT_POSITION)
        }
    }

    private fun initResetButton() = binding.btnReset.apply {
        setOnClickListener {
            setResetButtonAction()
        }
    }
    private fun setResetButtonAction() = binding.apply {
        editSearch.text?.clear()
        galleryViewModel.resetGallery()
        recyclerViewSmoothTopScroll(true)
        dropDownSoftKeyboard(requireActivity(), inputManager)
    }


    private fun subscribeToObservers() = binding.apply {
        with(galleryViewModel) {
            galleryList.observe(viewLifecycleOwner) {
                it?.let {
                    submitDataToAdapters(it)
                    recyclerViewSmoothTopScroll(!saved)
                }
            }
        }
    }
    private fun submitDataToAdapters(data: List<Gallery>) {
        val submitList = data.sortedByDescending { image ->
            image.createdAt
        }
        albumTypeItemAdapter.submitData(submitList)
        listTypeItemAdapter.submitData(submitList)

        checkDataEmpty(data.isEmpty())
    }
    private fun checkDataEmpty(isEmpty: Boolean) {
        if(isEmpty) {
            if (param.isBlank()) return
            showToast(
                requireContext(),
                MESSAGE_RESULT_SEARCH_FAIL
            )
        }
    }

    private var saved = false
    private fun navigateToGalleryDetails(gallery: Gallery) {
        saved = true
        val action = GalleryFragmentDirections.actionGalleryFragmentToGalleryDetailsFragment(
            gallery
        )
        findNavController().navigate(action)
    }

    override fun onStop() {
        if(!saved) {
            binding.apply {
                editSearch.text?.clear()
                btnAlbum.isChecked = true
            }
        }
        super.onStop()
    }

}