package com.xten.sara.ui.my

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentMyBinding
import com.xten.sara.util.LoginUtils
import com.xten.sara.util.constants.MESSAGE_RESULT_LOGOUT_FAIL
import com.xten.sara.util.constants.MESSAGE_RESULT_LOGOUT_SUCCESS
import com.xten.sara.util.constants.SURVEY_URL
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFragment : Fragment() {

    private lateinit var binding: FragmentMyBinding

    @Inject
    lateinit var gso: GoogleSignInOptions
    private val googleSignInClient by lazy { GoogleSignIn.getClient(requireActivity(), gso) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_my, container, false)
        setBinding()
        return binding.root
    }

    private fun setBinding() = binding.apply {
        lifecycleOwner = viewLifecycleOwner
        fragent = this@MyFragment
        account = GoogleSignIn.getLastSignedInAccount(requireContext())!!
    }

    fun setCollectionButtonAction(email: String) {
        val action = MyFragmentDirections.actionMyFragmentToMyGalleryFragment(email)
        findNavController().navigate(action)
    }

    fun setSurveyButtonAction() {
        Intent(Intent.ACTION_VIEW, Uri.parse(SURVEY_URL)).run(::startActivity)
    }

    @Inject
    lateinit var prefs : SharedPreferences
    fun setLogoutButtonAction() = googleSignInClient.signOut()
        .addOnSuccessListener {
            handleLogoutSuccess()
        }
        .addOnFailureListener {
            handleLogoutFail()
        }

    private fun handleLogoutSuccess() {
        LoginUtils.setLoginState(prefs, false)
        LoginUtils.clearToken(prefs)
        showToast(requireContext(), MESSAGE_RESULT_LOGOUT_SUCCESS)
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val options = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, false).build()
        findNavController().navigate(R.id.action_myFragment_to_loginFragment, null, options)
    }
    private fun handleLogoutFail() {
        showToast(requireContext(), MESSAGE_RESULT_LOGOUT_FAIL)
    }

}