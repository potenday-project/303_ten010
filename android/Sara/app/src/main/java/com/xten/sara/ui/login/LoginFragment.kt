package com.xten.sara.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.showToast
import com.xten.sara.databinding.FragmentLoginBinding
import com.xten.sara.util.LoginUtils
import com.xten.sara.util.constants.MESSAGE_WARNING_ERROR
import com.xten.sara.util.constants.State
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var gso: GoogleSignInOptions
    private val googleSignInClient by lazy { GoogleSignIn.getClient(requireActivity(), gso) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerOnBackPressedDispatcher()
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_login, container, false)
        return setBinding().root
    }

    private fun setBinding() = binding.apply {
        lifecycleOwner = viewLifecycleOwner
        viewModel = loginViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        subscribeToObserver()
    }

    private fun registerOnBackPressedDispatcher() = requireActivity().onBackPressedDispatcher
        .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

    private fun initView() = binding.apply {
        btnLogin.setOnClickListener {
            setLoginButtonAction()
        }
    }

    private fun setLoginButtonAction() = binding.apply {
        googleSignInOnClientLauncher.launch(googleSignInClient.signInIntent)
    }
    private val googleSignInOnClientLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val task= GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val result = task.getResult(ApiException::class.java)
            handleSignInOnClientTask(result)
        } catch (e: Exception) {
            showToast(requireContext(), MESSAGE_WARNING_ERROR)
        }
    }

    private fun handleSignInOnClientTask(result: GoogleSignInAccount) {
        loginViewModel.requestLogin(result.email, result.displayName, result.photoUrl.toString())
    }

    private fun subscribeToObserver() = loginViewModel.state.observe(viewLifecycleOwner) {
        when(it) {
            State.SUCCESS -> handleResultSuccess()
            State.FAIL -> handleResultFail()
            else -> return@observe
        }
    }

    private fun handleResultSuccess() {
        navigateToHome()
    }
    private fun handleResultFail() {
        showToast(requireContext(), MESSAGE_WARNING_ERROR)
    }

    @Inject
    lateinit var prefs : SharedPreferences
    private fun navigateToHome() {
        val isChecked = binding.btnAutoLogin.isChecked
        LoginUtils.setLoginState(prefs, isChecked)
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

}