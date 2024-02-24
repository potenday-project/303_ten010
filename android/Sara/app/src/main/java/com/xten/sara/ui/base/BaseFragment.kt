package com.xten.sara.ui.base

import android.content.*
import android.net.Uri
import android.os.*
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-06-01
 * @desc
 */
abstract class BaseFragment<B: ViewDataBinding>(@LayoutRes private val layoutId: Int) : Fragment() {

    private var _binding: B? = null
    protected val binding: B = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setBinding(inflater, container)
        registerOnBackPressedCallback()
        return binding.root
    }

    private fun setBinding(inflater: LayoutInflater, container: ViewGroup?) {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        setupBinding(binding)
        setData()
    }

    abstract fun setupBinding(binding: B): B

    protected open fun setData() = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGlobalVariables()
        initView()
        subscribeToObservers()
    }

    protected open fun initGlobalVariables() = Unit

    abstract fun initView()

    protected open fun subscribeToObservers() = Unit

    protected fun showToastMessage(str: String) = Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()

    protected fun navigateToBrowser(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url)).run(::startActivity)

    protected fun navigateToDirections(action: NavDirections) {
        findNavController().navigate(action)
    }
    protected fun navigateToDirections(id: Int) {
        findNavController().navigate(id)
    }

    private fun registerOnBackPressedCallback() = requireActivity().onBackPressedDispatcher.addCallback(
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setOnBackPressedListener()
            }
        }
    )

    open fun setOnBackPressedListener() {
        findNavController().popBackStack()
    }


    override fun onDestroyView() {
        _binding = null
        destroyGlobalVariables()
        super.onDestroyView()
    }

    protected open fun destroyGlobalVariables() = Unit
}