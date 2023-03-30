package com.xten.sara.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentImageResultBinding
import com.xten.sara.util.MESSAGE_RESULT_SAVE_FAIL
import com.xten.sara.util.MESSAGE_RESULT_SAVE_SUCCESS
import com.xten.sara.util.State

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
        subscribeToObserver()
    }

    private fun initView() = binding.apply {
        btnBack.setOnClickListener {
            setBackButtonAction()
        }
        btnSave.setOnClickListener {
            setSaveButtonAction()
        }
        btnRecall.setOnClickListener {
            setRecallButtonAction()
        }
    }

    private fun setBackButtonAction() {
        findNavController().navigate(R.id.action_imageResultFragment_to_homeFragment)
    }

    private fun setSaveButtonAction() {
        imageUploadViewModel.saveContent()
    }
    private fun setRecallButtonAction()  = imageUploadViewModel.apply {
        setState(State.FAIL)
        controlProgress(false)
        requestChatGPT()
    }

    private fun subscribeToObserver() = imageUploadViewModel.apply {
        state.observe(viewLifecycleOwner) {
            if(it == State.SUCCESS) {
                controlProgress(true)
            }
        }
        saveResult.observe(viewLifecycleOwner) {
            it?.let {
                showToast(
                    requireContext(),
                    when(it) {
                        State.SUCCESS.name -> MESSAGE_RESULT_SAVE_SUCCESS
                        State.FAIL.name -> MESSAGE_RESULT_SAVE_FAIL
                        else -> return@observe
                    }
                )
            }
        }
    }

    private fun controlProgress(end: Boolean) = binding.progressView.motionLayout.apply {
        if(end) setTransition(-1, -1)
        else {
            setTransition(R.id.rotation)
            transitionToEnd()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imageUploadViewModel.setState(State.NONE)
    }

}