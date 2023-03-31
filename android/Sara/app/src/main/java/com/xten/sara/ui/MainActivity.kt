package com.xten.sara.ui

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.xten.sara.R
import com.xten.sara.SaraApplication.Companion.dropdownSoftKeyboard
import com.xten.sara.databinding.ActivityMainBinding
import com.xten.sara.util.*
import com.xten.sara.util.constants.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()
    }

    private fun initBottomNavigation() = binding.navBottom.apply {
        setupWithNavController(getNavController())
        setOnItemReselectedListener { /* NO-OP */ }
    }
    private fun getNavController() = run {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.apply {
            graph.setStartDestination(R.id.homeFragment)
            addOnDestinationChangedListener { _, destination, _ ->
                setNavigateDestinationChangeAction(destination.label)
            }
        }
    }
    private fun setNavigateDestinationChangeAction(label: CharSequence?) {
        setWindowTransparent(label)
        controlBottomNavVisibility(label)
    }

    @Suppress("DEPRECATION")
    private fun setWindowTransparent(label: CharSequence?) = label?.let {
        window?.apply {
            statusBarColor = when (it) {
                LABEL_IMAGE_RESULT_ -> Color.TRANSPARENT
                else -> Color.WHITE
            }
            decorView.systemUiVisibility = when(label) {
                LABEL_IMAGE_RESULT_ -> SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else -> SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


    private fun controlBottomNavVisibility(label: CharSequence?) = label?.let {
        binding.navBottom.visibility = when(label) {
            LABEL_HOME_, LABEL_GALLERY_, LABEL_MY_ -> VISIBLE
            else -> GONE
        }
    }


    @Inject
    lateinit var inputManager: InputMethodManager
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.run {
            currentFocus?.let {
                val rect = Rect()
                it.getGlobalVisibleRect(rect)
                if (!rect.contains(x.toInt(), y.toInt())) dropdownSoftKeyboard(this@MainActivity, inputManager)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}