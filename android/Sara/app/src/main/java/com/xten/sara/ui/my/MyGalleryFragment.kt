package com.xten.sara.ui.my

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xten.sara.R
import com.xten.sara.SaraApplication
import com.xten.sara.SaraApplication.Companion.dropDownSoftKeyboard
import com.xten.sara.data.Gallery
import com.xten.sara.databinding.FragmentMyGalleryBinding
import com.xten.sara.ui.gallery.GalleryFragmentDirections
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
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_my_gallery, container, false)
        return setBinding().root
    }

    private fun setBinding() = binding.apply {
        lifecycleOwner = viewLifecycleOwner
        viewModel = galleryViewModel
        galleryViewModel.updateGallery(args.email)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        subscribeToObservers()
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
                Log.e(TAG, "$it: ", )
                navigateToGalleryDetails(it)
            }
        }
    }
    private fun initView() = binding.apply {
        setRecyclerViewItemType(TYPE_ALBUM)

        btnBack.setOnClickListener {
            setBackButtonAction()
        }

        btnAlbum.setOnCheckedChangeListener { _, isChecked ->
            setTypeButtonAction(isChecked)
        }

        editSearch.setOnKeyListener { _, keyCode, event ->
            when(keyCode) {
                KeyEvent.KEYCODE_ENTER -> handleEnterKeyEvent()
                else -> return@setOnKeyListener false
            }
        }

        btnReset.setOnClickListener {
            setResetButtonAction()
        }

    }

    private fun setBackButtonAction() {
        findNavController().popBackStack()
    }

    @Inject
    lateinit var inputManager: InputMethodManager
    private fun handleEnterKeyEvent(): Boolean = binding.editSearch.run {
        dropDownSoftKeyboard(requireActivity(), inputManager)
        galleryViewModel.apply {
            val input = getCurInput()
            if(input.isNotBlank()) {
                requestSearch()
                binding.recyclerView.scrollToPosition(DEFAULT_POSITION)
            }
            else SaraApplication.showToast(requireContext(), MESSAGE_WARNING_EDIT)
        }

        true
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

    private fun setTypeButtonAction(isChecked: Boolean) = when {
        isChecked -> setRecyclerViewItemType(TYPE_ALBUM)
        else -> setRecyclerViewItemType(TYPE_LIST)
    }

    private fun setResetButtonAction() = binding.apply {
        editSearch.text?.clear()
        galleryViewModel.resetGallery()
        dropDownSoftKeyboard(requireActivity(), inputManager)
        recyclerView.smoothScrollToPosition(DEFAULT_POSITION)
    }

    private fun subscribeToObservers() = binding.apply {
        with(galleryViewModel) {
            galleryList.observe(viewLifecycleOwner) {
                if(!saved) binding.recyclerView.scrollToPosition(DEFAULT_POSITION)
                it?.let {
                    val list = it.sortedByDescending { image ->
                        image.createdAt
                    }
                    albumTypeItemAdapter.submitData(list)
                    listTypeItemAdapter.submitData(list)

                    if(getCurInput().isBlank()) return@let
                    if(list.isEmpty()) SaraApplication.showToast(
                        requireContext(),
                        MESSAGE_RESULT_SEARCH_FAIL
                    )
                }
            }
        }
    }

    private var saved = false
    private fun navigateToGalleryDetails(gallery: Gallery) {
        saved = true
        val action = MyGalleryFragmentDirections.actionMyGalleryFragmentToGalleryDetailsFragment(
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