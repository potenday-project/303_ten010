package com.xten.sara.ui.home

import android.content.ClipboardManager
import android.content.Intent
import android.os.*
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.*
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.xten.sara.R
import com.xten.sara.SaraApplication
import com.xten.sara.SaraApplication.Companion.dropDownSoftKeyboard
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentImageResultBinding
import com.xten.sara.util.constants.*
import com.xten.sara.util.view.*
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
        return getBinding(container).root
    }

    private fun getBinding(container: ViewGroup?) : FragmentImageResultBinding {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_image_result, container, false)
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = imageUploadViewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initKeyboardState()
        subscribeToObserver()
    }

    private fun initView() = binding.apply {

        initContentView()
        initSaveButton()
        initRequestEditText()
        initRecallButton()
        initShareButton()
        initBackButton()

    }

    private fun initContentView() = binding.contentView.apply {
        setOnClickListener {
            setContentViewClickAction()
        }
    }
    @Inject
    lateinit var clipboardManager: ClipboardManager
    private lateinit var content: String
    private fun setContentViewClickAction() {
        content = binding.content.text.trim().toString()
        if(content.isBlank()) return
        SaraApplication.copyToClipboard(requireContext(), clipboardManager, content)
    }

    private fun initSaveButton() = binding.btnSave.apply {
        setOnClickListener {
            setSaveButtonAction()
        }
    }
    @Inject
    lateinit var inputManager: InputMethodManager
    private fun setSaveButtonAction() = binding.apply{
        with(textField) {
            when (visibility) {
                View.GONE -> {
                    visibility = View.VISIBLE
                    requestFocus()
                }
                else -> {
                    dropDownSoftKeyboard(requireActivity(), inputManager)
                    requestSaveContent()
                }
            }
        }
    }
    private fun requestSaveContent() {
        val text = binding.editRequest.text.toString().trim()
        if(text.isBlank()) {
            showToast(requireContext(), MESSAGE_WARNING_EDIT)
            return
        }
        imageUploadViewModel.requestSaveContent(text)
    }

    private fun initRequestEditText() = binding.editRequest.apply {
        imageUploadViewModel.initFreeText()
        setOnFocusChangeListener { _, hasFocus ->
            setFocusChangeAction(hasFocus)
        }
        doAfterTextChanged {
            setTextFieldError(it)
        }
    }
    private fun setFocusChangeAction(hasFocus: Boolean) {
        binding.textField.isHintEnabled = hasFocus
    }
    private fun setTextFieldError(input: Editable?) = input?.let {
        if(input.length < MAX_TEXT_TITLE_LENGTH) return@let
        binding.textField.error = TEXT_FIELD_ERROR_MESSAGE_TITLE
    }

    private fun initRecallButton() = binding.btnRecall.apply {
        setOnClickListener {
            setRecallButtonAction()
        }
    }
    private var isRecalled = false
    private fun setRecallButtonAction()  {
        with(imageUploadViewModel) {
            isRecalled = true
            setLoadingState(State.ING)
            requestChatGPT()
        }
        setProgressViewState(false)
    }

    private fun setProgressViewState(end: Boolean) = binding.progressView.apply {
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

    private fun initShareButton() = binding.btnShare.apply {
        setOnClickListener {
            setShareButtonAction()
        }
    }
    private fun setShareButtonAction() {
        startChooseIntent()
    }
    private fun startChooseIntent() = Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/*"
        putExtra(Intent.EXTRA_TEXT, content)
        putExtra(Intent.EXTRA_TITLE, SHARE_TITLE_TEXT)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }, null).run(::startActivity)


    private lateinit var softInputAssist: SoftInputAssist
    private fun initKeyboardState() {
        softInputAssist = SoftInputAssist(requireActivity())

        initKeyboardVisibilityUtils()
    }
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private fun initKeyboardVisibilityUtils() {
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
            onShowKeyboard = { keyboardHeight ->
                binding.scrollView.run {
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            smoothScrollTo(scrollX, scrollY + keyboardHeight)
                        }, 200L
                    )
                }
            }
        )
    }


    private fun subscribeToObserver() = imageUploadViewModel.apply {
        loadingState.observe(viewLifecycleOwner) {
            when(it) {
                State.SUCCESS -> handleAnalysisResultSuccess()
                State.FAIL -> handleAnalysisResultFail()
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

    private fun handleAnalysisResultSuccess() {
        setProgressViewState(true)
    }

    private fun handleAnalysisResultFail() {
        findNavController().popBackStack()
        showToast(requireContext(), MESSAGE_RESULT_AI_FAIL)
    }

    private fun handleSaveResultSuccess() {
        with(binding) {
            textField.visibility = View.GONE
            editRequest.text?.clear()
        }
        showToast(requireContext(), MESSAGE_RESULT_SAVE_SUCCESS)
    }
    private fun handleSaveResultFail() {
        showToast(requireContext(), MESSAGE_RESULT_SAVE_FAIL)
    }

    private fun initBackButton() = binding.btnBack.apply {
        setOnClickListener {
            setBackButtonAction()
        }
    }
    private fun setBackButtonAction() {
        with(imageUploadViewModel) {
            initQueryType()
            initFreeText()
            initViewModel()
        }
        val options = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, false).build()
        findNavController().navigate(R.id.action_imageResultFragment_to_homeFragment, null, options)
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
            setProgressViewState(true)
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

    override fun onDestroyView() {
        super.onDestroyView()
        keyboardVisibilityUtils.detachKeyboardListeners()
        with(imageUploadViewModel) {
            if(getCurLoadingState() == State.ING) return
            initViewModel()
        }
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