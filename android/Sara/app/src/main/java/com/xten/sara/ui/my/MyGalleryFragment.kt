package com.xten.sara.ui.my

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.xten.sara.R
import com.xten.sara.databinding.FragmentMyGalleryBinding
import com.xten.sara.ui.gallery.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint


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
        setBinding()
        return binding.root
    }

    private fun setBinding() = binding.apply {
        lifecycleOwner = viewLifecycleOwner
        galleryViewModel.updateGallery(args.email)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        subscribeToObserver()
    }


    private fun initView() {

    }

    private fun subscribeToObserver() = galleryViewModel.apply {
        galleryList.observe(viewLifecycleOwner) {
            binding.textView.text = "$it"
        }
    }


}