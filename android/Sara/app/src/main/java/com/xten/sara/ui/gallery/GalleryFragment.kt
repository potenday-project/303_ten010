package com.xten.sara.ui.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.xten.sara.data.Gallery
import com.xten.sara.databinding.FragmentGalleryBinding
import com.xten.sara.util.constants.GRID_COL_TYPE_1
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private val galleryViewModel: GalleryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryBinding.inflate(layoutInflater)
        galleryViewModel.updateGallery()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        subscribeToObservers()
    }


    private val galleryItemAdapter by lazy {
        GalleryItemAdapter().apply {
            setOnItemClickListener {
                navigateToGalleryDetails(it)
            }
        }
    }
    private fun initView() = binding.apply {
        with(recyclerView) {
            val gridLayoutManager = GridLayoutManager(requireContext(), GRID_COL_TYPE_1)
            layoutManager = gridLayoutManager
            adapter = galleryItemAdapter
        }
    }

    private fun subscribeToObservers() = binding.apply {
        with(galleryViewModel) {
            galleryList.observe(viewLifecycleOwner) {
                it?.let {
                    val list = it.sortedByDescending { image ->
                        image.createdAt
                    }
                    galleryItemAdapter.submitData(list)
                }
            }
        }
    }

    private fun navigateToGalleryDetails(gallery: Gallery) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToGalleryDetailsFragment(
            gallery
        )
        findNavController().navigate(action)
    }


}