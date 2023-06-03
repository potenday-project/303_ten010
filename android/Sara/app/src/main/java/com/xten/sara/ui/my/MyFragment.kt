package com.xten.sara.ui.my

import android.content.SharedPreferences
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.*
import com.xten.sara.R
import com.xten.sara.databinding.FragmentMyBinding
import com.xten.sara.ui.base.BaseFragment
import com.xten.sara.util.LoginUtils
import com.xten.sara.util.constants.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFragment : BaseFragment<FragmentMyBinding>(R.layout.fragment_my) {

    override fun setupBinding(binding: FragmentMyBinding): FragmentMyBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragent = this@MyFragment
            account = GoogleSignIn.getLastSignedInAccount(requireContext())
        }
    }

    @Inject
    lateinit var gso: GoogleSignInOptions
    private var googleSignInClient: GoogleSignInClient? = null
    override fun initGlobalVariables() {
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun initView() = Unit

    fun navigateToCollection(email: String) {
        val action = MyFragmentDirections.actionMyFragmentToMyGalleryFragment(email)
        findNavController().navigate(action)
    }

    fun navigateToSurveyUrl() = navigateToBrowser(SURVEY_URL)

    fun logout() = googleSignInClient?.let {
        it.signOut().addOnSuccessListener { handleLogoutSuccess() }
            .addOnFailureListener { handleLogoutFail() }
    }

    @Inject
    lateinit var prefs : SharedPreferences
    private fun handleLogoutSuccess() {
        LoginUtils.setLoginState(prefs, false)
        LoginUtils.clearToken(prefs)
        showToastMessage(MESSAGE_RESULT_LOGOUT_SUCCESS)
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val options = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, false).build()
        findNavController().navigate(R.id.action_myFragment_to_loginFragment, null, options)
    }

    private fun handleLogoutFail() {
        showToastMessage(MESSAGE_RESULT_LOGOUT_FAIL)
    }

    override fun destroyGlobalVariables() {
        super.destroyGlobalVariables()
        googleSignInClient = null
    }

}