package com.xten.sara.ui.home

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.textclassifier.TextClassifier.NO_OP
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xten.sara.R
import com.xten.sara.SaraApplication
import com.xten.sara.SaraApplication.Companion.dropdownSoftKeyboard
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentImageResultBinding
import com.xten.sara.util.ImageFileUtils
import com.xten.sara.util.constants.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
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
        imageUploadViewModel.initFreeText()

        btnBack.setOnClickListener {
            setBackButtonAction()
        }
        btnSave.setOnClickListener {
            setSaveButtonAction()
        }

        setupEditRequestEvents()

        btnRecall.setOnClickListener {
            setRecallButtonAction()
        }
    }

    private fun setBackButtonAction() {
        val options = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, false).build()
        findNavController().navigate(R.id.action_imageResultFragment_to_homeFragment, null, options)
    }

    private fun setSaveButtonAction() {
        accordingToTextFieldVisibility()
    }

    @Inject
    lateinit var inputManager: InputMethodManager
    private fun accordingToTextFieldVisibility() = binding.textField.apply {
        when(visibility) {
            View.GONE -> {
                visibility = View.VISIBLE
                requestFocus()
            }
            else -> {
                dropdownSoftKeyboard(requireActivity(), inputManager)
                verifyRequestState()
            }
        }
    }

    private fun setupEditRequestEvents() = binding.apply {
        with(editRequest) {
            setOnFocusChangeListener { _, hasFocus ->
                setFocusChangeAction(hasFocus)
            }
            doAfterTextChanged {
                setTextFieldError(it)
            }
        }
    }
    private fun setFocusChangeAction(hasFocus: Boolean) = binding.apply {
        if (hasFocus) {
            imageUploadViewModel.setQueryType(TYPE_4)
        }
        textField.isHintEnabled = hasFocus
    }
    private fun setTextFieldError(input: Editable?) = input?.let {
        if(input.length < MAX_TEXT_LENGTH) return@let
        binding.textField.error = TEXT_FIELD_ERROR_MESSAGE
    }

    private fun verifyRequestState() = binding.apply {
        if(editRequest.text.toString().trim().isBlank()) {
            showToast(requireContext(), MESSAGE_WARNING_EDIT)
            return@apply
        }
        imageUploadViewModel.saveContent()
    }

    private fun setRecallButtonAction()  = imageUploadViewModel.apply {
        setState(State.ING)
        controlProgress(false)
        requestChatGPT()
    }

    private fun subscribeToObserver() = imageUploadViewModel.apply {
        state.observe(viewLifecycleOwner) {
            when(it) {
                State.SUCCESS -> handleResultSuccess()
                State.FAIL -> handleResultFail()
                else -> return@observe
            }
        }
        saveResult.observe(viewLifecycleOwner) {
            it?.let {
                when(it) {
                    State.SUCCESS.name -> handleSaveResultSuccess()
                    State.FAIL.name -> handleSaveResultFail()
                    else -> return@observe
                }
            }
        }
    }

    private fun handleResultSuccess() {
        controlProgress(true)
    }
    private fun handleResultFail() {
        findNavController().popBackStack()
        showToast(requireContext(), MESSAGE_RESULT_AI_FAIL)
    }
    private fun controlProgress(end: Boolean) = binding.progressView.motionLayout.apply {
        if(end) setTransition(NO_OP, NO_OP)
        else {
            setTransition(NO_OP, NO_OP)
            setTransition(R.id.rotation)
            transitionToEnd()
        }
    }

    private fun handleSaveResultSuccess() {
        binding.textField.visibility = View.GONE
        showToast(requireContext(), MESSAGE_RESULT_SAVE_SUCCESS)
        imageUploadViewModel.initFreeText()
    }
    private fun handleSaveResultFail() {
        showToast(requireContext(), MESSAGE_RESULT_SAVE_FAIL)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        with(imageUploadViewModel) {
            if(getCurState() == State.ING) {
                showToast(requireContext(), MESSAGE_CANCEL) //취소 로직 추가
            }
            initViewModel()
        }
    }


    companion object {
        private const val NO_OP = -1
    }

}