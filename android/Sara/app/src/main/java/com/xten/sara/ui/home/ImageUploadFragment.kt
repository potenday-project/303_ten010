package com.xten.sara.ui.home

import android.app.Activity
import android.content.Intent
import android.content.Intent.createChooser
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.dropdownSoftKeyboard
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentImageUploadBinding
import com.xten.sara.util.ImageFileUtils
import com.xten.sara.util.constants.*
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
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_image_upload, container, false)
        return setBinding().root
    }

    private fun setBinding() = binding.apply {
        lifecycleOwner = viewLifecycleOwner
        fragment = this@ImageUploadFragment
        viewModel = imageUploadViewModel
        initImageUri(args.imageUri)
    }

    private lateinit var imageUri: Uri
    private fun initImageUri(uri: Uri) {
        imageUri = uri
        imageUploadViewModel.setImageUri(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = binding.apply {

        imageUploadViewModel.initFreeText()
        radio1.isChecked = true

        image.setOnClickListener {
            setImageClickAction()
        }

        radio4.setOnCheckedChangeListener { _, isChecked ->
            setRadioButtonCheckedChangeAction(isChecked)
        }

        setupEditRequestEvents()

        btnRequest.setOnClickListener {
            setupRequestButtonAction()
        }

        btnClose.setOnClickListener {
            setCloseButtonAction()
        }

    }

    private fun setImageClickAction()  {
        createChooser()
    }

    private fun setRadioButtonCheckedChangeAction(isChecked: Boolean) = binding.apply {
        with(textField) {
            if(isChecked) {
                visibility = View.VISIBLE
                //포커스 관련 추가하기
            } else visibility = View.GONE
        }
    }

    private lateinit var cache: File
    private fun createChooser() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            resolveActivity(requireContext().packageManager)?.let {
                createCacheFile()
                putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            }
        }
        requestBringImageUriLauncher.launch(ImageFileUtils.createChooserIntent(cameraIntent))
    }

    private fun createCacheFile() {
        cache = ImageFileUtils.createTempFile(requireContext())
        imageUri = ImageFileUtils.getTempFileUri(requireContext(), cache)
    }


    private val requestBringImageUriLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode != Activity.RESULT_OK) {
            ImageFileUtils.deleteTempFile(cache)
            return@registerForActivityResult
        }
        val uri = it.data?.data
        uri?.let { uri->
            imageUri = uri
            ImageFileUtils.deleteTempFile(cache)
        }
        setImageUri(imageUri)
        return@registerForActivityResult
    }

    private fun setImageUri(uri: Uri) {
        imageUri = uri
        imageUploadViewModel.setImageUri(imageUri)
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

    @Inject
    lateinit var inputManager: InputMethodManager
    fun activeRadioButton(num: Int) = binding.apply {
        imageUploadViewModel.setQueryType(num)
    }

    // !--request
    private fun setupRequestButtonAction() {
        dropdownSoftKeyboard(requireActivity(), inputManager)
        verifyRequestState()
    }

    private fun verifyRequestState() = binding.apply {
        if(radio4.isChecked && editRequest.text.toString().trim().isBlank()) {
            showToast(requireContext(), MESSAGE_WARNING_EDIT)
            return@apply
        }
        requestImageAnalysis()
    }

    private fun requestImageAnalysis() = imageUri.let {
        val path = ImageFileUtils.getAbsolutePath(
            requireContext(),
            it
        )
        imageUploadViewModel.requestImageAnalysis(path)
        navigateToImageResult()
    }

    private fun navigateToImageResult() = imageUri.let {
        val options = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, false).build()
        findNavController().navigate(R.id.action_imageUploadFragment_to_imageResultFragment, null, options)
    }

    private fun setCloseButtonAction() {
        findNavController().popBackStack()
    }

}