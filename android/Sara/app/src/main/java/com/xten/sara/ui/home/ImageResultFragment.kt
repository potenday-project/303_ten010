package com.xten.sara.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.xten.sara.R
import com.xten.sara.databinding.FragmentImageResultBinding

class ImageResultFragment : Fragment() {

    private lateinit var binding: FragmentImageResultBinding
    private val imageUploadViewModel : ImageUploadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_image_result, container, false)
        return setBinding().root
    }

    private fun setBinding() = binding.apply {
        lifecycleOwner = viewLifecycleOwner
        viewModel = imageUploadViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setBackButtonAction()
    }

    private fun initView() = binding.apply {
        btnBack.setOnClickListener {
            setBackButtonAction()
        }
    }

    private fun setBackButtonAction() {
        findNavController().navigate(R.id.action_imageResultFragment_to_homeFragment)
    }

}