package com.xten.sara.ui.login

import android.content.SharedPreferences
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.xten.sara.R
import com.xten.sara.util.LoginUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed(
            { navigationToDestination() },
            DELAY_DURATION
        )
    }

    private fun navigationToDestination() = findNavController().apply{
        val options = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, false).build()
        val token = LoginUtils.getToken(prefs)
        navigate(
            resId = token?.run { R.id.action_splashFragment_to_homeFragment } ?: R.id.action_splashFragment_to_loginFragment,
            args = null,
            navOptions = options
        )
    }

    companion object {
        const val DELAY_DURATION = 1000L
    }

}