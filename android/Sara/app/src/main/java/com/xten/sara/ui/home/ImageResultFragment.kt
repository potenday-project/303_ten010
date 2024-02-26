package com.xten.sara.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.*
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.common.*
import com.example.common.State
import com.xten.sara.R
import com.xten.sara.databinding.FragmentImageResultBinding
import com.xten.sara.extensions.connectWithTextField
import com.xten.sara.extensions.dropDownSoftKeyboard
import com.xten.sara.ui.base.BaseFragment
import com.xten.sara.util.ImageFileUtils
import com.xten.sara.util.view.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageResultFragment : BaseFragment<FragmentImageResultBinding>(R.layout.fragment_image_result) {

    private val viewModel : ImageResultViewModel by viewModels()
    private val args: ImageResultFragmentArgs by navArgs()

    override fun setupBinding(binding: FragmentImageResultBinding): FragmentImageResultBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@ImageResultFragment.viewModel
            fragment = this@ImageResultFragment
        }
    }

    override fun setData() {
        args.uri?.let {
            val path = ImageFileUtils.getAbsolutePath(requireContext(), it)
            viewModel.requestImageAnalysis(path, args.type)
        }
    }

    override fun initView() = binding.run {
        image.setImageURI(args.uri)
        editRequest.connectWithTextField(textField)
    }

    @Inject
    lateinit var inputManager: InputMethodManager
    fun onSaveButtonClick(textFieldVisibility: Int) = when(textFieldVisibility) {
        View.VISIBLE -> {
            inputManager.dropDownSoftKeyboard(requireActivity())
            val param = binding.editRequest.text.toString().trim()
            isValidInput(param)
        }
        else -> with(binding.textField) {
            requestFocus()
            visibility = View.VISIBLE
        }
    }

    // 입력검사
    private fun isValidInput(param: String) {
        when {
            param.isNotBlank() -> viewModel.requestSaveContent(param, args.type)
            else -> showToastMessage(MESSAGE_WARNING_EDIT)
        }
    }


    private var retry = false
    fun retryRequestImageAnalysis()  {
        retry = true
        showProgress(show = true)
        viewModel.requestChatGPT(args.type)
    }

    private fun showProgress(show: Boolean) = binding.progressView.run {
        animationView.run {
            if(show) playAnimation() else pauseAnimation()
        }
        with(motionLayout) {
            setTransition(NO_OP, NO_OP)
            if(show) setTransition(R.id.rotation).also { transitionToEnd() }
        }
    }


    private var softInputAssist: SoftInputAssist? = null
    private var keyboardVisibilityUtils: KeyboardVisibilityUtils? = null
    override fun initGlobalVariables() {
        softInputAssist = SoftInputAssist(requireActivity())
        keyboardVisibilityUtils = KeyboardVisibilityUtils(
            requireActivity().window,
            onShowKeyboard = { keyboardHeight -> binding.scrollView.run { smoothScrollTo(scrollX, scrollY + keyboardHeight) } }
        )
    }

    override fun subscribeToObservers() = viewModel.run {
        loadingState.observe(viewLifecycleOwner) {
            handleLoadingState(it)
        }
        saveResult.observe(viewLifecycleOwner) {
            handleSaveResult(it)
        }
    }

    private fun handleLoadingState(state: State) {
        if(state == State.SUCCESS) handleAnalysisResultSuccess()
        if(state == State.FAIL) handleAnalysisResultFail()
    }

    private fun handleAnalysisResultSuccess() {
        showProgress(show = true)
    }

    private fun handleAnalysisResultFail() {
        showToastMessage(MESSAGE_RESULT_AI_FAIL)
        setOnBackPressedListener()
    }

    private fun handleSaveResult(state: State) {
        if(state == State.SUCCESS) handleSaveResultSuccess()
        if(state == State.FAIL) handleSaveResultFail()
    }

    private fun handleSaveResultSuccess() {
        with(binding) {
            textField.visibility = View.GONE
            editRequest.text?.clear()
        }
        showToastMessage(MESSAGE_RESULT_SAVE_SUCCESS)
    }

    private fun handleSaveResultFail() {
        showToastMessage(MESSAGE_RESULT_SAVE_FAIL)
    }

    fun onPopUpToBackStack() {
        val options = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, false).build()
        findNavController().navigate(R.id.action_imageResultFragment_to_homeFragment, null, options)
    }

    override fun setOnBackPressedListener() = when(binding.progressView.motionLayout.visibility) {
        View.VISIBLE -> cancelRequest()
        else -> onPopUpToBackStack()
    }

    @Inject
    lateinit var clipboardManager: ClipboardManager
    fun copyToClipboard(clipboardManager: ClipboardManager, text: String) {
        if(text.isNotBlank()) {
            val clipData = ClipData.newPlainText(APP_NAME, text)
            clipboardManager.setPrimaryClip(clipData)
            showToastMessage(MESSAGE_TEXT_COPY)
        }
    }

    private fun cancelRequest() {
        viewModel.cancelRequest()
        showToastMessage(MESSAGE_CANCEL)

        if(retry) showProgress(show = true)
        else super.setOnBackPressedListener()
    }

    override fun onResume() {
        softInputAssist?.onResume()
        super.onResume()
    }

    override fun onPause() {
        softInputAssist?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        viewModel.cancelRequest()
        super.onDestroyView()
    }

    override fun destroyGlobalVariables() {
        super.destroyGlobalVariables()
        softInputAssist?.onDestroy()
        softInputAssist = null
        keyboardVisibilityUtils?.detachKeyboardListeners()
        keyboardVisibilityUtils = null
    }

    companion object {
        private const val NO_OP = -1
    }

}