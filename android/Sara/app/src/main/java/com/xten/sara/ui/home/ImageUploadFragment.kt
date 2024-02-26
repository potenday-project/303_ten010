package com.xten.sara.ui.home

import android.net.Uri
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.navArgs
import com.example.common.MESSAGE_WARNING_EDIT
import com.example.common.QueryType
import com.xten.sara.R
import com.xten.sara.databinding.FragmentImageUploadBinding
import com.xten.sara.extensions.connectWithTextField
import com.xten.sara.extensions.dropDownSoftKeyboard
import com.xten.sara.ui.base.ImagePickupBaseFragment
import com.xten.sara.util.view.KeyboardVisibilityUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageUploadFragment : ImagePickupBaseFragment<FragmentImageUploadBinding>(R.layout.fragment_image_upload) {

    private val args : ImageUploadFragmentArgs by navArgs()

    override fun setupBinding(binding: FragmentImageUploadBinding): FragmentImageUploadBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@ImageUploadFragment
            updateImageUri(args.imageUri)
        }
    }

    private var imageUri: Uri? = null
    override fun setData() {
        updateImageUri(args.imageUri)
    }

    override fun updateImageUri(uri: Uri?) {
        this.imageUri = uri
        binding.image.setImageURI(uri)
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
        image.setImageURI(imageUri)
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
            else -> navigateToImageResult(param)
        }
    }

    fun handleFreeTypeCheckedChange(isChecked: Boolean) {
        inputManager.dropDownSoftKeyboard(requireActivity())
        binding.textField.visibility = if(isChecked) View.VISIBLE else View.GONE
    }

    fun changeImage() {
        binding.editRequest.clearFocus()
        startGalleryChooserIntent()
    }

    private fun navigateToImageResult(param: String) = imageUri?.let {
        var str: String? = null
        val type = when {
            binding.radio1.isChecked -> QueryType.ESSAY.type()
            binding.radio2.isChecked -> QueryType.POEM.type()
            binding.radio3.isChecked -> QueryType.EVALUATION.type()
            else -> {
                str = param
                QueryType.FREE.type()
            }
        }
        val action = ImageUploadFragmentDirections.actionImageUploadFragmentToImageResultFragment(
            uri = it,
            type = type,
            str = str
        )
        navigateToDirections(action)
    }

    override fun destroyGlobalVariables() {
        super.destroyGlobalVariables()
        keyboardVisibilityUtils?.detachKeyboardListeners()
        keyboardVisibilityUtils = null
    }

}