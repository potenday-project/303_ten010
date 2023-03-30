package com.xten.sara.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.xten.sara.R
import com.xten.sara.SaraApplication
import com.xten.sara.SaraApplication.Companion.dropdownSoftKeyboard
import com.xten.sara.databinding.FragmentImageUploadBinding
import com.xten.sara.util.ImageFileUtils
import com.xten.sara.util.MESSAGE_WARNING_EDIT
import com.xten.sara.util.TAG
import com.xten.sara.util.TYPE_4
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageUploadFragment : Fragment() {

    private lateinit var binding: FragmentImageUploadBinding
    private val imageUploadViewModel : ImageUploadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_image_upload, container, false)
        return setBinding().root
    }

    private fun setBinding() = binding.apply {
        lifecycleOwner = viewLifecycleOwner
        fragment = this@ImageUploadFragment
        viewModel = imageUploadViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImageUri()
        initView()
    }

    private fun initView() = binding.apply {
        setupImageView()
        setupEditRequestEvent()
        btnRequest.setOnClickListener {
            setupRequestButtonAction()
        }
        btnClose.setOnClickListener {
            setupCloseButtonAction()
        }
    }

    private var imageUri: Uri? = null
    private fun initImageUri() {
        imageUri = imageUploadViewModel.imageUri.value
    }

    private fun setupImageView() = binding.image.setOnClickListener {
        createChooser()
    }

    private fun createChooser() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            resolveActivity(requireContext().packageManager)?.let {
                setImageUri()
                putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            }
        }
        requestBringImageUriLauncher.launch(ImageFileUtils.createChooserIntent(cameraIntent))
    }

    private fun setImageUri() : Uri? {
        Log.e(TAG, "setImageUri: ", )
        imageUri = ImageFileUtils.getTempFileUri(requireContext())
        return imageUri
    }

    private val requestBringImageUriLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val uri = it.data?.data
        uri?.let { uri->
            imageUri = uri
        }
        imageUploadViewModel.setImageUri(imageUri)
    }

    val chipState = MutableLiveData(true)
    private fun setupEditRequestEvent() = binding.apply {
        with(editRequest) {
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    imageUploadViewModel.setQueryType(TYPE_4)
                    chipState.value = !hasFocus
                }
                textField.isHintEnabled = hasFocus
            }
            doAfterTextChanged {
                it?.let {
                    textField.error = if (it.length >= MAX_TEXT_LENGTH) TEXT_FIELD_ERROR_MESSAGE else null
                }
            }
        }
    }

    @Inject
    lateinit var inputManager: InputMethodManager
    fun activeRadioButton(num: Int) = binding.apply {
        chipState.value = true
        imageUploadViewModel.setQueryType(num)
        if(!chipState.value!!) dropdownSoftKeyboard(requireActivity(), inputManager)
    }

    // !--request
    private fun setupRequestButtonAction() = binding.apply {
        dropdownSoftKeyboard(requireActivity(), inputManager)
        verifyRequestState()
    }

    private fun verifyRequestState() = binding.run {
        if(chipState.value!!) {
            requestImageAnalysis()
            return@run
        }
        if(editRequest.text.toString().trim().isBlank()) {
            SaraApplication.showToast(requireContext(), MESSAGE_WARNING_EDIT)
            return@run
        }
        requestImageAnalysis()
    }

    private fun requestImageAnalysis() = imageUri?.let {
        val path = ImageFileUtils.getAbsolutePath(
            requireContext(),
            it
        )
        Log.e(TAG, "requestImageAnalysis: $path", )
        imageUploadViewModel.requestImageAnalysis(
            path
        )
        navigateToImageResult()
    }

    private fun navigateToImageResult() {
        findNavController().navigate(R.id.action_imageUploadFragment_to_imageResultFragment)
    }

    private fun setupCloseButtonAction() {
        findNavController().popBackStack()
    }


    companion object {
        private const val TEXT_FIELD_ERROR_MESSAGE = "최대 30글자를 넘을 수 없습니다."
        private const val MAX_TEXT_LENGTH = 30
    }

}