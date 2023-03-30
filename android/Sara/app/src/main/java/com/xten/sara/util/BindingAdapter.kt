package com.xten.sara.util

import android.net.Uri
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.xten.sara.R

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
        }
    }

    @JvmStatic
    @BindingAdapter("ChipColorState")
    fun setChipColorState(button: RadioButton, state: Boolean?)  {
        state?.let {
            with(button) {
                backgroundTintList = resources.getColorStateList(
                    if(state) R.color.chip_bg_color else R.color.sara_unable,
                    null)
                setTextColor(
                    resources.getColorStateList(
                        if(state) R.color.chip_text_color else R.color.sara_gray,
                        null
                    )
                )
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
        url?.let {
            Glide.with(imageView)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }
    }


}