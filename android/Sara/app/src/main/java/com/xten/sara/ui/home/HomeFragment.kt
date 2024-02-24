package com.xten.sara.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.*
import androidx.activity.result.contract.ActivityResultContracts
import com.example.common.*
import com.xten.sara.R
import com.xten.sara.databinding.FragmentHomeBinding
import com.xten.sara.ui.base.ImagePickupBaseFragment
import com.xten.sara.util.ImageFileUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : ImagePickupBaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override fun setupBinding(binding: FragmentHomeBinding): FragmentHomeBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            home = this@HomeFragment
            num = getRandomNum()
        }
    }
    private fun getRandomNum() = (com.example.common.DEFAULT_ until com.example.common.RANDOM_SIZE).random()

    override fun initView() = Unit

    //!-- request permissions : 1. StorageAccess Permission 2. Camera Permission
    fun checkPermissionsToImageUpload() = when {
        Environment.isExternalStorageManager() -> requestCameraPermission()
        else -> requestStorageAccessPermission()
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> handleCameraPermissionResult(isGranted) }

    private fun handleCameraPermissionResult(isGranted: Boolean) = when {
        isGranted -> startGalleryChooserIntent()
        else -> showToastMessage(MESSAGE_PERMISSION_CAMERA)
    }

    private fun requestCameraPermission() = cameraPermissionLauncher.launch(Manifest.permission.CAMERA)

    private var imageUri: Uri? = null
    override fun updateImageUri(uri: Uri?) {
        this.imageUri = uri
    }

    override fun handleImagePickResultOk(intent: Intent?): Uri? {
        val uri = super.handleImagePickResultOk(intent) ?: imageUri
        navigateToImageUpload(uri)
        return uri
    }

    private fun navigateToImageUpload(uri: Uri?) = uri?.let {
        val action = HomeFragmentDirections.actionHomeFragmentToImageUploadFragment(it)
        navigateToDirections(action)
    }

    private fun requestStorageAccessPermission() {
        showToastMessage(MESSAGE_PERMISSION_ACCESS_FILE)
        storageAccessPermissionLauncher.launch(ImageFileUtils.createFileAccessSettingsIntent(requireContext()))
    }

    private val storageAccessPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { handleStoragePermissionResult() }

    private fun handleStoragePermissionResult() = when {
        Environment.isExternalStorageManager() -> requestCameraPermission()
        else -> showToastMessage(MESSAGE_PERMISSION_ACCESS_FILE)
    }

    fun changeSaraLogo() {
        binding.num = getRandomNum()
    }

    fun navigateToSearchUrl() = navigateToBrowser(SEARCH_URL)

    fun navigateToSurveyUrl() = navigateToBrowser(SURVEY_URL)

    override fun setOnBackPressedListener() = requireActivity().finishAffinity()

    companion object {
        private const val SEARCH_URL = "https://www.pinterest.co.kr"
    }

}