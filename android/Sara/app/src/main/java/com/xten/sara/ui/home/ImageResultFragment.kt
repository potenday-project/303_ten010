package com.xten.sara.ui.home

import android.content.ClipboardManager
import android.content.Intent
import android.os.*
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.xten.sara.R
import com.xten.sara.SaraApplication
import com.xten.sara.SaraApplication.Companion.dropDownSoftKeyboard
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentImageResultBinding
import com.xten.sara.util.constants.*
import com.xten.sara.util.view.KeyboardVisibilityUtils
import com.xten.sara.util.view.SoftInputAssist
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
        registerOnBackPressedDispatcher()
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
        setKeyboardState()
    }

    private lateinit var softInputAssist: SoftInputAssist
    private fun setKeyboardState() {
        softInputAssist = SoftInputAssist(requireActivity())

        setKeyboardVisibilityUtils()
    }

     private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
     private fun setKeyboardVisibilityUtils() {
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
             onShowKeyboard = { keyboardHeight ->
                 binding.scrollView.run {
                     Handler(Looper.getMainLooper()).postDelayed(
                         {
                             smoothScrollTo(scrollX, scrollY + keyboardHeight)
                         }, 200L
                     )
                 }
             })
     }

    private fun initView() = binding.apply {
        imageUploadViewModel.initFreeText()

        btnBack.setOnClickListener {
            setBackButtonAction()
        }

        contentView.setOnClickListener {
            setContentViewClickAction()
        }

        btnSave.setOnClickListener {
            setSaveButtonAction()
        }

        setupEditRequestEvents()

        btnRecall.setOnClickListener {
            setRecallButtonAction()
        }

        btnShare.setOnClickListener {
            setShareButtonAction(content.text.toString())
        }

    }

    private fun setBackButtonAction() {
        val options = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, false).build()
        imageUploadViewModel.apply {
            initQueryType()
            initFreeText()
            initViewModel()
        }
        findNavController().navigate(R.id.action_imageResultFragment_to_homeFragment, null, options)
    }

    @Inject
    lateinit var clipboardManager: ClipboardManager
    private fun setContentViewClickAction() {
        val text = binding.content.text.trim().toString()
        if(text.isBlank()) return
        SaraApplication.copyToClipboard(requireContext(), clipboardManager, text)
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
                dropDownSoftKeyboard(requireActivity(), inputManager)
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
        textField.isHintEnabled = hasFocus
    }
    private fun setTextFieldError(input: Editable?) = input?.let {
        if(input.length < MAX_TEXT_TITLE_LENGTH) return@let
        binding.textField.error = TEXT_FIELD_ERROR_MESSAGE_TITLE
    }

    private fun verifyRequestState() = binding.apply {
        if(editRequest.text.toString().trim().isBlank()) {
            showToast(requireContext(), MESSAGE_WARNING_EDIT)
            return@apply
        }
        imageUploadViewModel.saveContent()
    }

    private var isRecalled = false
    private fun setRecallButtonAction()  = imageUploadViewModel.apply {
        isRecalled = true
        setState(State.ING)
        controlProgress(false)
        requestChatGPT()
    }

    private fun setShareButtonAction(text: String) {
        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/*"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_TITLE, SHARE_TITLE_TEXT)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }, null)
        startActivity(share)
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
    private fun controlProgress(end: Boolean) = binding.progressView.apply {
        if(end) {
            motionLayout.setTransition(NO_OP, NO_OP)
            animationView.pauseAnimation()
        } else {
            with(motionLayout) {
                setTransition(NO_OP, NO_OP)
                setTransition(R.id.rotation)
                transitionToEnd()
            }
            animationView.playAnimation()
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
        keyboardVisibilityUtils.detachKeyboardListeners()
        with(imageUploadViewModel) {
            if(getCurState() == State.ING) return
            initViewModel()
        }
    }

    private fun registerOnBackPressedDispatcher() = requireActivity().onBackPressedDispatcher
        .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when(binding.progressView.motionLayout.visibility) {
                    View.VISIBLE -> cancelRequest()
                    else -> setBackButtonAction()
                }
            }
        })

    private fun cancelRequest() {
        imageUploadViewModel.cancelRequest()
        showToast(requireContext(), MESSAGE_CANCEL)

        if(isRecalled) {
            controlProgress(true)
            return
        }
       findNavController().popBackStack()
    }

    override fun onResume() {
        softInputAssist.onResume()
        super.onResume()
    }

    override fun onPause() {
        softInputAssist.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        softInputAssist.onDestroy()
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroy()
    }

    companion object {
        private const val NO_OP = -1
    }

}