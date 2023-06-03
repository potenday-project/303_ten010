package com.xten.sara.ui.home

import android.content.ClipboardManager
import android.content.Intent
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.*
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.xten.sara.R
import com.xten.sara.databinding.FragmentImageResultBinding
import com.xten.sara.databinding.ViewProgressBinding
import com.xten.sara.ui.base.BaseFragment
import com.xten.sara.util.constants.*
import com.xten.sara.util.view.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageResultFragment : BaseFragment<FragmentImageResultBinding>(R.layout.fragment_image_result) {

    private val imageUploadViewModel : ImageUploadViewModel by activityViewModels()

    override fun setupBinding(binding: FragmentImageResultBinding): FragmentImageResultBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = imageUploadViewModel
            fragment = this@ImageResultFragment
        }
    }

    override fun setData() {
        imageUploadViewModel.initFreeText()
    }

    override fun initView() = binding.run {
        editRequest.setup(textField, MAX_TEXT_TITLE_LENGTH)
    }

    @Inject
    lateinit var clipboardManager: ClipboardManager

    @Inject
    lateinit var inputManager: InputMethodManager
    fun onSaveButtonClick(textFieldVisibility: Int) = when(textFieldVisibility) {
        View.VISIBLE -> verifyInputState(inputManager)
        else -> with(binding.textField) {
            requestFocus()
            visibility = View.VISIBLE
        }
    }

    override fun verifyInputState(inputManager: InputMethodManager, isBlank: Boolean, input: String) {
        val param = binding.editRequest.text.toString().trim()
        super.verifyInputState(inputManager, param.isBlank(), param)
    }

    override fun hasVerifiedInputState(param: String) {
        imageUploadViewModel.requestSaveContent(param)
    }

    private var retry = false
    fun retryRequestImageAnalysis()  {
        retry = true
        binding.progressView.setProgressState(true)
        imageUploadViewModel.requestChatGPT()
    }

    private fun ViewProgressBinding.setProgressState(play: Boolean) {
        animationView.changeAnimation(play)
        with(motionLayout) {
            setTransition(NO_OP, NO_OP)
            if(play) setTransition(R.id.rotation).also { transitionToEnd() }
        }
    }
    private fun LottieAnimationView.changeAnimation(play: Boolean) {
        if(play) playAnimation() else pauseAnimation()
    }

    private var softInputAssist: SoftInputAssist? = null
    private var keyboardVisibilityUtils: KeyboardVisibilityUtils? = null
    override fun initGlobalVariables() {
        softInputAssist = SoftInputAssist(requireActivity())
        keyboardVisibilityUtils = getKeyboardVisibilityUtils(binding.scrollView)
    }

    override fun subscribeToObservers() = imageUploadViewModel.run {
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
        binding.progressView.setProgressState(true)
    }

    private fun handleAnalysisResultFail() {
        showToastMessage(MESSAGE_RESULT_AI_FAIL)
        setOnBackPressedListener()
    }

    private fun handleSaveResult(state: String?) {
        if(state == State.SUCCESS.name) handleSaveResultSuccess()
        if(state == State.FAIL.name) handleSaveResultFail()
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
        with(imageUploadViewModel) {
            initQueryType()
            initViewModel()
        }
        val options = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, false).build()
        findNavController().navigate(R.id.action_imageResultFragment_to_homeFragment, null, options)
    }

    override fun setOnBackPressedListener() = when(binding.progressView.motionLayout.visibility) {
        View.VISIBLE -> cancelRequest()
        else -> onPopUpToBackStack()
    }

    private fun cancelRequest() {
        imageUploadViewModel.cancelRequest()
        showToastMessage(MESSAGE_CANCEL)

        if(retry) binding.progressView.setProgressState(true)
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

    override fun destroyGlobalVariables() {
        super.destroyGlobalVariables()
        if(imageUploadViewModel.getCurLoadingState() != State.ING) imageUploadViewModel.initViewModel()
        softInputAssist?.onDestroy()
        softInputAssist = null
        keyboardVisibilityUtils?.detachKeyboardListeners()
        keyboardVisibilityUtils = null
    }

    companion object {
        private const val NO_OP = -1
    }

}