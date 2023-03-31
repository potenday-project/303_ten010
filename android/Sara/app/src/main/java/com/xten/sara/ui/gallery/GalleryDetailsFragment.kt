package com.xten.sara.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentGalleryDetailsBinding
import com.xten.sara.util.constants.MESSAGE_RESULT_DELETE_FAIL
import com.xten.sara.util.constants.MESSAGE_RESULT_DELETE_SUCCESS
import com.xten.sara.util.constants.State
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GalleryDetailsFragment : Fragment() {

    private lateinit var binding: FragmentGalleryDetailsBinding
    private val args :GalleryDetailsFragmentArgs by navArgs()

    private lateinit var account : GoogleSignInAccount

    private val galleryViewModel : GalleryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_gallery_details, container, false)
        account = GoogleSignIn.getLastSignedInAccount(requireContext())!!
        setBinding()
        return binding.root
    }

    private fun setBinding() = binding.apply {
        lifecycleOwner = viewLifecycleOwner
        gallery = args.gallery
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        subscribeToObserver()
    }

    private fun initView() = binding.apply {
        btnBack.setOnClickListener {
            setBackButtonAction()
        }
        btnShare.setOnClickListener {
            setShareButtonAction()
        }
        with(btnRemove) {
            visibility = if(args.gallery!!.email == account.email) View.VISIBLE else View.GONE
            setOnClickListener {
                setRemoveButtonAction()
            }
        }
    }

    private fun setBackButtonAction() {
        findNavController().popBackStack()
    }

    private fun setShareButtonAction() {

    }

    private fun setRemoveButtonAction() {
        galleryViewModel.deleteContent(args.gallery!!._id)
    }

    private fun subscribeToObserver() = galleryViewModel.deleteResult.observe(viewLifecycleOwner) {
        it?.let {
            when (it) {
                State.SUCCESS.name -> executeAfterSuccess()
                State.FAIL.name -> executeAfterFail()
                else -> return@observe
            }
        }
    }

    private fun executeAfterSuccess() {
        showToast(requireContext(), MESSAGE_RESULT_DELETE_SUCCESS)
        setBackButtonAction()
    }

    private fun executeAfterFail() {
        showToast(requireContext(), MESSAGE_RESULT_DELETE_FAIL)
    }

}