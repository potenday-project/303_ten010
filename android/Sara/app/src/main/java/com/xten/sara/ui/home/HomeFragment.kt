package com.xten.sara.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentHomeBinding
import com.xten.sara.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment() : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val imageUploadViewModel : ImageUploadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerOnBackPressedDispatcher()
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, container, false)
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            num = (DEFAULT_ until RANDOM_SIZE).random()
            return root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = binding.apply {
        btnUpload.setOnClickListener {
            requestPermissionForImageUpload()
        }
        btnSearch.setOnClickListener {
            navigateToBrowser()
        }
    }


    // !-- request permission
    private fun requestPermissionForImageUpload() = when {
        Environment.isExternalStorageManager() -> requestCameraPermission(true)
        else -> {
            showToast(requireContext(), MESSAGE_PERMISSION_ACCESS_FILE)
            requestFileAccessStoragePermission.launch(ImageFileUtils.createFileAccessSettingsIntent(requireContext()))
        }
    }
    private fun requestCameraPermission(isGranted: Boolean) = when {
        isGranted -> requestCameraPermission.launch(Manifest.permission.CAMERA)
        else -> showToast(requireContext(), MESSAGE_PERMISSION_ACCESS_FILE)
    }
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> createChooser()
            else -> showToast(requireContext(), MESSAGE_PERMISSION_CAMERA)
        }
    }
    private val requestFileAccessStoragePermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        requestCameraPermission(Environment.isExternalStorageManager())
    }

    // !-- request image uri
    private var imageUri: Uri? = null
    private fun createChooser() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            resolveActivity(requireContext().packageManager)?.let {
                setImageUri()
                putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            }
        }
        requestBringImageUriLauncher.launch(ImageFileUtils.createChooserIntent(cameraIntent))
    }

    private fun setImageUri() = run {
        ImageFileUtils.getTempFileUri(requireContext())
    }.also { imageUri = it }

    // !-- send image uri
    private val requestBringImageUriLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val uri = it.data?.data
        uri?.let { uri->
            imageUri = uri
        }

        navigateToImageUpload()
    }

    private fun navigateToImageUpload() = imageUri?.let {
        imageUploadViewModel.setImageUri(imageUri)
        findNavController().navigate(R.id.action_homeFragment_to_imageUploadFragment)
    }

    private fun navigateToBrowser() = Intent(Intent.ACTION_VIEW, Uri.parse(SEARCH_URL)).run(::startActivity)

    private fun registerOnBackPressedDispatcher() = requireActivity().onBackPressedDispatcher
        .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
    )

    companion object {
        private const val SEARCH_URL = "https://www.pinterest.co.kr"
    }

}