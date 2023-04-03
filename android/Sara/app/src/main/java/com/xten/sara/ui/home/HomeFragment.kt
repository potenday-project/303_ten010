package com.xten.sara.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentHomeBinding
import com.xten.sara.util.ImageFileUtils
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
        return getBinding(container).root
    }

    private fun registerOnBackPressedDispatcher() = requireActivity().onBackPressedDispatcher
        .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

    private fun getBinding(container: ViewGroup?) : FragmentHomeBinding {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, container, false)
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            num = getRandomNum()
        }
    }
    private fun getRandomNum() = (DEFAULT_ until RANDOM_SIZE).random()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView()  {
        initSaraLogo()
        initUploadButton()
        initSearchButton()
        initSurveyButton()
    }

    private fun initSaraLogo() = binding.saraLogo.apply {
        setOnClickListener {
            setSaraLogoClickAction()
        }
    }
    private fun setSaraLogoClickAction() {
        binding.num = getRandomNum()
    }

    private fun initUploadButton() = binding.btnUpload.apply{
        setOnClickListener {
            setUploadButtonAction()
        }
    }
    private fun setUploadButtonAction() {
        checkPermissionToUpload()
    }

    // !-- request permission
    private fun checkPermissionToUpload() {
        val isGrantedFileAccessPermission = Environment.isExternalStorageManager()
        when {
            isGrantedFileAccessPermission -> hasGrantedFileAccessPermission()
            else -> isNotGrantedFileAccessPermission()
        }
    }
    private fun hasGrantedFileAccessPermission() {
        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> hasGrantedCameraPermission()
            else -> hasNotGrantedCameraPermission()
        }
    }

    private fun hasGrantedCameraPermission() {
        startChooserIntent()
    }

    private fun startChooserIntent() {
        val chooserIntent = ImageFileUtils.createChooserIntent(getCameraIntent())
        chooserIntentLauncher.launch(chooserIntent)
    }

    private lateinit var imageUri: Uri
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
                navigateToImageUpload()
            }
            else -> isResultCancel()
        }
    }
    private fun checkData(data: Uri?) = data?.let {
        imageUri = data
        ImageFileUtils.deleteTempFile(cache)
    }
    private fun navigateToImageUpload() {
        val action = HomeFragmentDirections.actionHomeFragmentToImageUploadFragment(
            imageUri = this.imageUri
        )
        findNavController().navigate(action)
    }
    private fun isResultCancel() {
        ImageFileUtils.deleteTempFile(cache)
    }

    private fun hasNotGrantedCameraPermission() {
        showToast(requireContext(), MESSAGE_PERMISSION_CAMERA)
    }

    private fun isNotGrantedFileAccessPermission() {
        showToast(requireContext(), MESSAGE_PERMISSION_ACCESS_FILE)
        requestFileAccessPermission()
    }
    private fun requestFileAccessPermission() {
        requestAgainFileAccessPermission.launch(ImageFileUtils.createFileAccessSettingsIntent(requireContext()))
    }
    private val requestAgainFileAccessPermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        when {
            Environment.isExternalStorageManager() -> hasGrantedFileAccessPermission()
            else -> notGrantedFileAccessPermission()
        }
    }

    private fun notGrantedFileAccessPermission() {
        showToast(requireContext(), MESSAGE_PERMISSION_ACCESS_FILE)
    }

    private fun initSearchButton() = binding.btnSearch.apply {
        setOnClickListener {
            setSearchButtonAction()
        }
    }
    private fun setSearchButtonAction() =
        Intent(Intent.ACTION_VIEW, Uri.parse(SEARCH_URL))
            .run(::startActivity)

    private fun initSurveyButton()  = binding.btnSurvey.apply {
        setOnClickListener {
            setSurveyButtonAction()
        }
    }
    private fun setSurveyButtonAction() =
        Intent(Intent.ACTION_VIEW, Uri.parse(SURVEY_URL))
            .run(::startActivity)

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding.num = getRandomNum()
    }

    companion object {
        private const val SEARCH_URL = "https://www.pinterest.co.kr"
    }

}