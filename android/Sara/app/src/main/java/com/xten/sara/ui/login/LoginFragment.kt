package com.xten.sara.ui.login

import android.content.Intent
import android.content.SharedPreferences
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.xten.sara.R
import com.xten.sara.databinding.FragmentLoginBinding
import com.xten.sara.ui.base.BaseFragment
import com.xten.sara.util.LoginUtils
import com.example.common.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var gso: GoogleSignInOptions
    private val googleSignInClient by lazy { GoogleSignIn.getClient(requireActivity(), gso) }

    override fun setupBinding(binding: FragmentLoginBinding): FragmentLoginBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@LoginFragment
            viewModel = loginViewModel
        }
    }

    override fun initView() = Unit

    fun login() = googleSignInOnClientLauncher.launch(googleSignInClient.signInIntent)
    private val googleSignInOnClientLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { handleSignInOnClientTask(it.data) }

    private fun handleSignInOnClientTask(data: Intent?) = try {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val result = task.getResult(ApiException::class.java)
        handleSignInOnClientTaskSuccess(result)
    } catch (e: Exception) {
        handleSignInOnClientTaskFail()
    }

    private fun handleSignInOnClientTaskSuccess(result: GoogleSignInAccount) {
        loginViewModel.requestLogin(result.email, result.displayName, result.photoUrl.toString())
    }
    private fun handleSignInOnClientTaskFail() {
        loginViewModel.setLoginState(State.FAIL)
    }

    override fun subscribeToObservers() {
        loginViewModel.loginState.observe(viewLifecycleOwner) {
            handleLoginState(it)
        }
    }

    private fun handleLoginState(state: State) {
        when(state) {
            State.SUCCESS -> {
                showToastMessage(MESSAGE_LOGIN_SUCCESS)
                saveAutoLogin()
                navigateToHome()
            }
            State.FAIL -> showToastMessage(MESSAGE_WARNING_ERROR)
            else -> Unit
        }
    }

    @Inject
    lateinit var prefs : SharedPreferences
    private fun saveAutoLogin() {
        val isChecked = binding.btnAutoLogin.isChecked
        LoginUtils.setLoginState(prefs, isChecked)
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    override fun setOnBackPressedListener() = requireActivity().finish()

}