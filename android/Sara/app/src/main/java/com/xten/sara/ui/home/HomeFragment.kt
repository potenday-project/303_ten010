package com.xten.sara.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentHomeBinding
import com.xten.sara.util.*
import com.xten.sara.util.constants.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class HomeFragment() : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerOnBackPressedDispatcher()
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, container, false)
        return setBinding().root
    }

    //!-- register back press : 현재 화면에서 백버튼 동작시 앱 종료
    private fun registerOnBackPressedDispatcher() = requireActivity().onBackPressedDispatcher
        .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

    private fun setBinding() = binding.apply {
        lifecycleOwner = viewLifecycleOwner
        num = (DEFAULT_ until RANDOM_SIZE).random()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = binding.apply {
        btnUpload.setOnClickListener {
            setUploadButtonAction()
        }
        btnSearch.setOnClickListener {
            setSearchButtonAction()
        }
    }


    // !-- request permission
    private fun setUploadButtonAction() {
        val fileAccessPermissionGranted = Environment.isExternalStorageManager()
        requestPermissions(fileAccessPermissionGranted)
    }
    private fun requestPermissions(isGranted: Boolean) {
        if(isGranted) asGrantedFileAccessPermission()
        else asNotGrantedFileAccessPermission()
    }
    private fun asGrantedFileAccessPermission() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }
    private fun asNotGrantedFileAccessPermission() {
        showToast(requireContext(), MESSAGE_PERMISSION_ACCESS_FILE)
        requestFileAccessStoragePermission.launch(ImageFileUtils.createFileAccessSettingsIntent(requireContext()))
    }
    private val requestFileAccessStoragePermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(Environment.isExternalStorageManager()) asGrantedFileAccessPermission()
        else asAgainNotGrantedFileAccessPermission()
    }
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> when {
            isGranted -> asGrantedCameraPermission()
            else -> asNotGrantedCameraPermission()
        }
    }
    private fun asAgainNotGrantedFileAccessPermission() {
        showToast(requireContext(), MESSAGE_PERMISSION_ACCESS_FILE)
    }
    private fun asNotGrantedCameraPermission() {
        showToast(requireContext(), MESSAGE_PERMISSION_CAMERA)
    }

    // !-- request image uri
    private var imageUri: Uri? = null
    private lateinit var cache: File
    private fun asGrantedCameraPermission() {
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

    // !-- send image uri
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

        navigateToImageUpload()
    }

    private fun navigateToImageUpload() = imageUri?.let {
        val action = HomeFragmentDirections.actionHomeFragmentToImageUploadFragment(
            imageUri = it
        )
        findNavController().navigate(action)
    } ?: showToast(requireContext(), MESSAGE_RESULT_UPLOAD_FAIL)

    private fun setSearchButtonAction() {
        Intent(Intent.ACTION_VIEW, Uri.parse(SEARCH_URL)).run(::startActivity)
    }

    companion object {
        private const val SEARCH_URL = "https://www.pinterest.co.kr"
    }

}