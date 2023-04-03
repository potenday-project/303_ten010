package com.xten.sara.ui.my

import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.*
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.dropDownSoftKeyboard
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.data.Gallery
import com.xten.sara.databinding.FragmentMyGalleryBinding
import com.xten.sara.ui.gallery.GalleryItemAdapter
import com.xten.sara.ui.gallery.GalleryViewModel
import com.xten.sara.util.constants.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MyGalleryFragment : Fragment() {

    private lateinit var binding: FragmentMyGalleryBinding

    private val galleryViewModel: GalleryViewModel by viewModels()
    private val args: MyGalleryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestUpdateGallery()
        return getBinding(container).root
    }
    private fun requestUpdateGallery() {
        galleryViewModel.updateGallery(args.email)
    }
    private fun getBinding(container: ViewGroup?) : FragmentMyGalleryBinding {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_my_gallery, container, false)
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
        initBackButton()
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
        recyclerViewTopSmoothScroll(false)
    }

    private fun recyclerViewTopSmoothScroll(isSmooth: Boolean) = binding.recyclerView.apply {
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
        recyclerViewTopSmoothScroll(true)
        dropDownSoftKeyboard(requireActivity(), inputManager)
    }

    private fun initBackButton() = binding.btnBack.apply {
        setOnClickListener {
            setBackButtonAction()
        }
    }
    private fun setBackButtonAction() {
        findNavController().popBackStack()
    }


    private fun subscribeToObservers() = binding.apply {
        with(galleryViewModel) {
            galleryList.observe(viewLifecycleOwner) {
                it?.let {
                    submitDataToAdapters(it)
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

    private fun navigateToGalleryDetails(gallery: Gallery) {
        val action = MyGalleryFragmentDirections.actionMyGalleryFragmentToGalleryDetailsFragment(
            gallery
        )
        findNavController().navigate(action)
    }

}