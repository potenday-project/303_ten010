package com.xten.sara.util

import android.content.Context
import android.net.Uri
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.xten.sara.R
import com.xten.sara.util.constants.*


/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-28
 * @desc
 */
object BindingAdapter {

    private val prefixArray = arrayOf("사진을 올려주면 ", "사진을 보여주면 ")
    @JvmStatic
    @BindingAdapter("PrefixRandomText")
    fun setPrefixRandomText(textView: TextView, num: Int?) {
        num?.let {
            textView.text = prefixArray[num]
        }
    }

    private val suffixArray = arrayOf("시를 써주는 Sara","글 써주는 AI, Sara")
    @JvmStatic
    @BindingAdapter("SuffixRandomText")
    fun setSuffixRandomText(textView: TextView, num: Int?) {
        num?.let {
            textView.text = suffixArray[num]
        }
    }

    @JvmStatic
    @BindingAdapter("Image")
    fun setImage(imageView: ImageView, uri: Uri?) {
        uri?.let {
            imageView.setImageURI(it)
            getScreenHeight(imageView.context)?.let { height ->
                imageView.maxHeight = height
            }
        }
    }

    @JvmStatic
    @BindingAdapter("QueryText")
    fun setQueryText(button: Button, type: QueryType?) {
        type?.let {
            button.text = "${type.desc()}"
        }
    }

    @JvmStatic
    @BindingAdapter("ImageLoad")
    fun setImage(imageView: ImageView, url: String?) {
        Glide.with(imageView)
            .load(url)
            .error(R.drawable.ic_sara_normal).apply {
                centerInside()
            }
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
        getScreenHeight(imageView.context)?.let { height ->
            imageView.maxHeight = height
        }
    }

    @JvmStatic
    @BindingAdapter("Type")
    fun convertType(textView: TextView, type: Int?) {
        type?.let {
            textView.text = when(type) {
                TYPE_1 -> QueryType.ESSAY.str()
                TYPE_2 -> QueryType.POEM.str()
                TYPE_3 -> QueryType.EVALUATION.str()
                else -> QueryType.FREE.str()
            }
        }
    }

    private fun getScreenHeight(context: Context) = run {
        val display = context.resources?.displayMetrics
        display?.heightPixels?.run {
            pxToDp(this, context).toInt()
        }
    }
    private fun pxToDp(px: Int, context: Context) = px / ((context.resources.displayMetrics.densityDpi.toFloat()) / DisplayMetrics.DENSITY_DEFAULT)



}