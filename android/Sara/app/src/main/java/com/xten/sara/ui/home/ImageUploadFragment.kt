package com.xten.sara.ui.home

import android.net.Uri
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.xten.sara.R
import com.xten.sara.databinding.FragmentImageUploadBinding
import com.xten.sara.ui.base.BaseFragment
import com.xten.sara.util.ImageFileUtils
import com.xten.sara.util.constants.MAX_TEXT_LENGTH
import com.xten.sara.util.view.KeyboardVisibilityUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageUploadFragment : BaseFragment<FragmentImageUploadBinding>(R.layout.fragment_image_upload) {

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

    @Inject
    lateinit var inputManager: InputMethodManager
    override fun initView() = binding.run {
        editRequest.setup(textField, MAX_TEXT_LENGTH)
        btnRequest.addOnVerifyInputListener(inputManager)
    }

    fun activateInputType(num: Int) = imageUploadViewModel.setQueryType(num)

    fun handleFreeTypeCheckedChange(isChecked: Boolean) {
        dropDownSoftKeyboard(inputManager)
        binding.textField.visibility = if(isChecked) View.VISIBLE else View.GONE
    }

    fun changeImage()  {
        binding.editRequest.clearFocus()
        startGalleryChooserIntent()
    }

    override fun updateImageUri(uri: Uri?){
        uri?.let { imageUploadViewModel.setImageUri(it) }
    }


    // !--request
    override fun verifyInputState(inputManager: InputMethodManager, isBlank: Boolean, param: String) {
        val verify = binding.radio4.isChecked && binding.editRequest.text.toString().trim().isBlank()
        super.verifyInputState(inputManager, verify, param)
    }

    override fun hasVerifiedInputState(param: String) {
        requestImageAnalysis()
    }

    private fun requestImageAnalysis() = imageUploadViewModel.imageUri.value?.let {
        val path = ImageFileUtils.getAbsolutePath(requireContext(), it)
        imageUploadViewModel.requestImageAnalysis(path)
        navigateToImageResult()
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