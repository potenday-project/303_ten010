package com.xten.sara.ui.base

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.*
import com.xten.sara.ui.gallery.GalleryItemAdapter
import com.xten.sara.util.ImageFileUtils
import com.xten.sara.util.constants.*
import com.xten.sara.util.view.KeyboardVisibilityUtils
import java.io.File

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


    //!-- view expand func

    private val TEXT_FIELD_ERROR_MESSAGE : (Int)->(String) = { num -> "최대 ${num}글자를 넘을 수 없습니다." }
    protected fun TextInputEditText.setup(textField: TextInputLayout, maxLength: Int = MAX_TEXT_LENGTH) {
        setOnFocusChangeListener { _, hasFocus ->
            textField.isHintEnabled = hasFocus
        }
        doAfterTextChanged {
            it?.run { if(length >= maxLength) textField.error = TEXT_FIELD_ERROR_MESSAGE(maxLength) }
        }
    }

    protected fun getKeyboardVisibilityUtils(scrollView: ScrollView) = KeyboardVisibilityUtils(requireActivity().window,
        onShowKeyboard = { keyboardHeight ->
            scrollView.run {
                Handler(Looper.getMainLooper()).postDelayed(
                    { smoothScrollTo(scrollX, scrollY + keyboardHeight) },
                    200L
                )
            }
        }
    )

    protected fun Button.addOnVerifyInputListener(inputManager: InputMethodManager) = setOnClickListener {
        verifyInputState(inputManager)
    }

    protected open fun verifyInputState(inputManager: InputMethodManager, isBlank: Boolean = false, param: String = "") {
        dropDownSoftKeyboard(inputManager)
        when {
            isBlank -> showToastMessage(MESSAGE_WARNING_EDIT)
            else -> hasVerifiedInputState(param)
        }
    }

    protected fun TextInputEditText.setup(inputManager: InputMethodManager) {
        editableText.clear()
        setOnKeyListener { _, keyCode, _ ->
            when(keyCode) {
                KeyEvent.KEYCODE_ENTER -> handleEnterKeyEvent(inputManager)
                else -> return@setOnKeyListener false
            }
        }
    }

    protected open fun handleEnterKeyEvent(inputManager: InputMethodManager) = true

    protected open fun hasVerifiedInputState(param: String) = Unit


    private var cache: File? = null
    protected fun startGalleryChooserIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            setCameraUri(requireContext())
        }
        val chooserIntent = ImageFileUtils.createChooserIntent(cameraIntent)
        chooserIntentLauncher.launch(chooserIntent)
    }
    private fun Intent.setCameraUri(context: Context) = resolveActivity(requireContext().packageManager)?.run {
        cache = ImageFileUtils.createCacheTempFile(requireContext())
        ImageFileUtils.getCacheTempFileUri(context, cache).also {
            updateImageUri(it)
            putExtra(MediaStore.EXTRA_OUTPUT, it)
        }
    }

    protected open fun updateImageUri(uri: Uri?) = Unit

    private val chooserIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == Activity.RESULT_OK) handleImagePickResultOk(it.data)
        cache?.delete()
    }

    // data가 null일 때 -> 카메라 픽업
    protected open fun handleImagePickResultOk(intent: Intent?) = intent?.data?.also {
        updateImageUri(it)
    }

    protected open fun GalleryItemAdapter.addOnItemClickListener() = Unit

    fun startShareChooserIntent(str: String) = Intent.createChooser(getShareIntent(str), null).run(::startActivity)
    private fun getShareIntent(str: String) = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/*"
        putExtra(Intent.EXTRA_TEXT, str)
        putExtra(Intent.EXTRA_TITLE, SHARE_TITLE_TEXT)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    protected fun showToastMessage(str: String) = Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()

    protected fun dropDownSoftKeyboard(inputManager: InputMethodManager) = activity?.currentFocus?.let{
        inputManager.hideSoftInputFromWindow(
            it.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
        it.clearFocus()
    }

    fun copyToClipboard(clipboardManager: ClipboardManager, text: String) {
        if(text.isNotBlank()) {
            val clipData = ClipData.newPlainText(APP_NAME, text)
            clipboardManager.setPrimaryClip(clipData)
            showToastMessage(MESSAGE_TEXT_COPY)
        }
    }

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