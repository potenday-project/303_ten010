package com.xten.sara.ui.login

import android.content.SharedPreferences
import android.os.*
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.xten.sara.R
import com.xten.sara.databinding.FragmentSplashBinding
import com.xten.sara.ui.base.BaseFragment
import com.xten.sara.util.LoginUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    override fun setupBinding(binding: FragmentSplashBinding): FragmentSplashBinding = binding

    override fun initView() {
        Handler(Looper.getMainLooper()).postDelayed(
            { navigationToDestination() },
            DELAY_DURATION
        )
    }

    @Inject
    lateinit var prefs: SharedPreferences
    private fun navigationToDestination() {
        val options = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, false).build()
        val loginState = LoginUtils.getLoginState(prefs)
        findNavController().navigate(
            resId = if(loginState) R.id.action_splashFragment_to_homeFragment  else R.id.action_splashFragment_to_loginFragment,
            args = null,
            navOptions = options
        )
    }

    override fun setOnBackPressedListener() = requireActivity().finish()

    companion object {
        const val DELAY_DURATION = 3000L
    }

}