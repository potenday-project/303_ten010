package com.xten.sara.ui.home

import android.net.Uri
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.common.MESSAGE_WARNING_EDIT
import com.xten.sara.R
import com.xten.sara.databinding.FragmentImageUploadBinding
import com.xten.sara.extensions.connectWithTextField
import com.xten.sara.extensions.dropDownSoftKeyboard
import com.xten.sara.ui.base.ImagePickupBaseFragment
import com.xten.sara.util.ImageFileUtils
import com.xten.sara.util.view.KeyboardVisibilityUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageUploadFragment : ImagePickupBaseFragment<FragmentImageUploadBinding>(R.layout.fragment_image_upload) {

    private val imageUploadViewModel : ImageUploadViewModel by activityViewModels()
    private val args : ImageUploadFragmentArgs by navArgs()

    override fun setupBinding(binding: FragmentImageUploadBinding): FragmentImageUploadBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@ImageUploadFragment
            viewModel = imageUploadViewModel
        }
    }

    override fun setData() {
        updateImageUri(args.imageUri)
    }

    private var keyboardVisibilityUtils: KeyboardVisibilityUtils? = null
    override fun initGlobalVariables() {
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
            onShowKeyboard = { keyboardHeight -> binding.scrollView.run { smoothScrollTo(scrollX, scrollY + keyboardHeight) } }
        )
    }

    override fun updateImageUri(uri: Uri?) {
        uri?.let { imageUploadViewModel.setImageUri(it) }
    }

    @Inject
    lateinit var inputManager: InputMethodManager
    override fun initView() = binding.run {
        editRequest.connectWithTextField(textField)
        btnRequest.setOnClickListener {
            onBtnRequestClicked()
        }
    }

    private fun onBtnRequestClicked() {
        inputManager.dropDownSoftKeyboard(requireActivity())
        val param = binding.editRequest.text.toString().trim()
        val isBlank = binding.radio4.isChecked && param.isBlank()
        when {
            isBlank -> showToastMessage(MESSAGE_WARNING_EDIT)
            else -> requestImageAnalysis()
        }
    }

    private fun requestImageAnalysis() = imageUploadViewModel.imageUri.value?.let {
        val path = ImageFileUtils.getAbsolutePath(requireContext(), it)
        imageUploadViewModel.requestImageAnalysis(path)
        navigateToImageResult()
    }

    fun activateInputType(num: Int) = imageUploadViewModel.setQueryType(num)

    fun handleFreeTypeCheckedChange(isChecked: Boolean) {
        inputManager.dropDownSoftKeyboard(requireActivity())
        binding.textField.visibility = if(isChecked) View.VISIBLE else View.GONE
    }

    fun changeImage() {
        binding.editRequest.clearFocus()
        startGalleryChooserIntent()
    }

    private var isSaved = false
    private fun navigateToImageResult() {
        isSaved = true
        navigateToDirections(R.id.action_imageUploadFragment_to_imageResultFragment)
    }

    override fun destroyGlobalVariables() {
        super.destroyGlobalVariables()
        keyboardVisibilityUtils?.detachKeyboardListeners()
        keyboardVisibilityUtils = null
        imageUploadViewModel.apply {
            if(!isSaved) initViewModel()
            initQueryType()
            initFreeText()
        }
    }

}