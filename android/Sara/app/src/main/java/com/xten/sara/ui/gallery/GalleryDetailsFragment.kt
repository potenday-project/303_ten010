package com.xten.sara.ui.gallery

import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.copyToClipboard
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.data.Gallery
import com.xten.sara.databinding.FragmentGalleryDetailsBinding
import com.xten.sara.util.constants.MESSAGE_RESULT_DELETE_FAIL
import com.xten.sara.util.constants.MESSAGE_RESULT_DELETE_SUCCESS
import com.xten.sara.util.constants.SHARE_TITLE_TEXT
import com.xten.sara.util.constants.State
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GalleryDetailsFragment : Fragment() {

    private lateinit var binding: FragmentGalleryDetailsBinding
    private val args :GalleryDetailsFragmentArgs by navArgs()

    private lateinit var account : GoogleSignInAccount

    private val galleryViewModel : GalleryViewModel by viewModels()
    private val gallery: Gallery? by lazy {
        args.gallery
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        account = GoogleSignIn.getLastSignedInAccount(requireContext())!!
        return getBinding(container).root
    }
    private fun getBinding(container: ViewGroup?) : FragmentGalleryDetailsBinding {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_gallery_details, container, false)
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            gallery = this@GalleryDetailsFragment.gallery
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        subscribeToObserver()
    }

    private fun initView() = binding.apply {
        initBackButton()
        initContentView()
        initShareButton()
        initButtonRemove()
    }

    private fun initBackButton() = binding.btnBack.apply {
        setOnClickListener {
            setBackButtonAction()
        }
    }
    private fun setBackButtonAction() {
        findNavController().popBackStack()
    }

    private fun initContentView() = binding.contentView.apply {
        setOnClickListener {
            setContentViewClickAction()
        }
    }
    @Inject
    lateinit var clipboardManager: ClipboardManager
    private fun setContentViewClickAction() = gallery?.let {
        val text = it.text!!.trim()
        if(text.isBlank()) return@let
        copyToClipboard(requireContext(), clipboardManager, text)
    }

    private fun initShareButton() = binding.btnShare.apply {
        setOnClickListener {
            setShareButtonAction()
        }
    }
    private fun setShareButtonAction() = gallery?.let {
        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/*"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(Intent.EXTRA_TITLE, SHARE_TITLE_TEXT)
            putExtra(Intent.EXTRA_TEXT, it.text)
        }, null)
        startActivity(share)
    }

    private fun initButtonRemove() = binding.btnRemove.apply {
        visibility = if(gallery?.email == account.email) View.VISIBLE else View.GONE
        setOnClickListener {
            setRemoveButtonAction()
        }
    }
    private fun setRemoveButtonAction() = gallery?.let {
        galleryViewModel.deleteContent(it._id!!)
    }


    private fun subscribeToObserver() = galleryViewModel.deleteResult.observe(viewLifecycleOwner) {
        it?.let {
            when (it) {
                State.SUCCESS.name -> handleResultSuccess()
                State.FAIL.name -> handleResultFail()
                else -> return@observe
            }
        }
    }
    private fun handleResultSuccess() {
        showToast(requireContext(), MESSAGE_RESULT_DELETE_SUCCESS)
        setBackButtonAction()
    }
    private fun handleResultFail() {
        showToast(requireContext(), MESSAGE_RESULT_DELETE_FAIL)
    }

}