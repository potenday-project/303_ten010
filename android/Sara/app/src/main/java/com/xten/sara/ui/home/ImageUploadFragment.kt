package com.xten.sara.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.dropDownSoftKeyboard
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentImageUploadBinding
import com.xten.sara.util.ImageFileUtils
import com.xten.sara.util.constants.MAX_TEXT_LENGTH
import com.xten.sara.util.constants.MESSAGE_WARNING_EDIT
import com.xten.sara.util.constants.TEXT_FIELD_ERROR_MESSAGE
import com.xten.sara.util.view.KeyboardVisibilityUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ImageUploadFragment : Fragment() {

    private lateinit var binding: FragmentImageUploadBinding
    private val imageUploadViewModel : ImageUploadViewModel by activityViewModels()

    private val args : ImageUploadFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return getBinding(container).root
    }
    private fun getBinding(container: ViewGroup?) : FragmentImageUploadBinding {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_image_upload, container, false)
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@ImageUploadFragment
            viewModel = imageUploadViewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImageUri()
        initView()
        initKeyboardVisibilityUtils()
    }

    private lateinit var imageUri: Uri
    private fun initImageUri() {
        imageUri = args.imageUri
        imageUploadViewModel.setImageUri(imageUri)
    }


    private fun initView() {
        initRadioButtons()
        initImageView()
        initEditRequest()
        initRequestButton()
        initCloseButton()
    }

    private fun initRadioButtons() = binding.apply {
        radio1.isChecked = true
        radio4.setOnCheckedChangeListener { _, isChecked ->
            setRadioButtonCheckedChangeAction(isChecked)
        }
    }
    fun activeRadioButton(num: Int) {
        imageUploadViewModel.setQueryType(num)
    }

    @Inject
    lateinit var inputManager: InputMethodManager
    private fun setRadioButtonCheckedChangeAction(isChecked: Boolean) {
        dropDownSoftKeyboard(requireActivity(), inputManager)
        binding.textField.visibility = if(isChecked) View.VISIBLE else View.GONE
    }

    private fun initImageView() = binding.image.apply{
        setOnClickListener {
            setImageClickAction()
        }
    }
    private fun setImageClickAction()  {
        binding.editRequest.clearFocus()
        startChooserIntent()
    }
    private fun startChooserIntent() {
        val chooserIntent = ImageFileUtils.createChooserIntent(getCameraIntent())
        chooserIntentLauncher.launch(chooserIntent)
    }

    private lateinit var cache: File
    private fun getCameraIntent() = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        resolveActivity(requireContext().packageManager)?.let {
            cache = ImageFileUtils.createCacheTempFile(requireContext())
            imageUri = ImageFileUtils.getCacheTempFileUri(requireContext(), cache)
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
    }

    private val chooserIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        when(it.resultCode) {
            Activity.RESULT_OK -> {
                checkData(it.data?.data)
                setImageUri()
            }
            else -> isResultCancel()
        }
    }
    private fun checkData(data: Uri?) = data?.let {
        imageUri = data
        ImageFileUtils.deleteTempFile(cache)
    }
    private fun setImageUri() {
        imageUploadViewModel.setImageUri(imageUri)
    }
    private fun isResultCancel() {
        ImageFileUtils.deleteTempFile(cache)
    }

    private fun initEditRequest() = binding.editRequest.apply{
        text?.clear()
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
        if(it.length < MAX_TEXT_LENGTH) return@let
        binding.textField.error = TEXT_FIELD_ERROR_MESSAGE
    }

    private fun initCloseButton() = binding.btnClose.apply{
        setOnClickListener {
            setCloseButtonAction()
        }
    }
    private fun setCloseButtonAction() {
        findNavController().popBackStack()
        imageUploadViewModel.apply {
            initQueryType()
            initFreeText()
        }
    }

    // !--request
    private fun initRequestButton() = binding.btnRequest.apply {
        setOnClickListener {
            setupRequestButtonAction()
        }
    }
    private fun setupRequestButtonAction() {
        dropDownSoftKeyboard(requireActivity(), inputManager)
        verifyRequestState()
    }

    private fun verifyRequestState() = binding.apply {
        if(radio4.isChecked && editRequest.text.toString().trim().isBlank()) {
            showToast(requireContext(), MESSAGE_WARNING_EDIT)
            return@apply
        }
        requestImageAnalysis()
    }

    private fun requestImageAnalysis()  {
        val path = ImageFileUtils.getAbsolutePath(
            requireContext(),
            imageUri
        )
        imageUploadViewModel.requestImageAnalysis(path)
        navigateToImageResult()
    }

    private var isSaved = false
    private fun navigateToImageResult() {
        isSaved = true
        findNavController().navigate(R.id.action_imageUploadFragment_to_imageResultFragment)
    }


    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private fun initKeyboardVisibilityUtils() {
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
            onShowKeyboard = { keyboardHeight ->
                binding.scrollView.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
            })
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        if(isSaved) imageUploadViewModel.initViewModel()
        super.onDestroyView()
    }

}