package com.xten.sara.ui.gallery

import android.content.ClipboardManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.xten.sara.R
import com.xten.sara.databinding.FragmentGalleryDetailsBinding
import com.xten.sara.ui.base.BaseFragment
import com.xten.sara.util.constants.MESSAGE_RESULT_DELETE_FAIL
import com.xten.sara.util.constants.MESSAGE_RESULT_DELETE_SUCCESS
import com.xten.sara.util.constants.State
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GalleryDetailsFragment : BaseFragment<FragmentGalleryDetailsBinding>(R.layout.fragment_gallery_details) {

    private val args :GalleryDetailsFragmentArgs by navArgs()
    private val galleryViewModel : GalleryViewModel by viewModels()
    @Inject lateinit var clipboardManager: ClipboardManager

    override fun setupBinding(binding: FragmentGalleryDetailsBinding): FragmentGalleryDetailsBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@GalleryDetailsFragment
            viewModel = galleryViewModel
            gallery = args.gallery
            myEmail = GoogleSignIn.getLastSignedInAccount(requireContext())?.email
        }
    }

    override fun initView() = Unit

    override fun subscribeToObservers() {
        galleryViewModel.deleteResult.observe(viewLifecycleOwner) {
            handleDeleteResult(it)
        }
    }

    private fun handleDeleteResult(result: String?) {
        if(result == State.SUCCESS.name) handleResultSuccess()
        if(result == State.FAIL.name) handleResultFail()
    }

    private fun handleResultSuccess() {
        showToastMessage(MESSAGE_RESULT_DELETE_SUCCESS)
        setOnBackPressedListener()
    }
    private fun handleResultFail() {
        showToastMessage(MESSAGE_RESULT_DELETE_FAIL)
    }

}