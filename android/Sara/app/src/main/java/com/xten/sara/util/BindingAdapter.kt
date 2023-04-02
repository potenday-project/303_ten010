package com.xten.sara.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
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

    private val logoImageResources = arrayOf(
        R.drawable.ic_sara_normal, R.drawable.ic_sara_supadupa,
        R.drawable.ic_sara_omg, R.drawable.ic_sara_study
    )
    @JvmStatic
    @BindingAdapter("RandomImage")
    fun setSuffixRandomImage(imageView: ImageView, num: Int?) {
        num?.let {
            val index = num + (DEFAULT_ until RANDOM_SIZE).random()
            imageView.setImageResource(logoImageResources[index])
        }
    }

    private val bubbleImageResources = arrayOf(
        R.drawable.ic_bubble_type1, R.drawable.ic_bubble_type2,
        R.drawable.ic_bubble_type3
    )
    @JvmStatic
    @BindingAdapter("RandomBubble")
    fun setSuffixRandomImageBubble(imageView: ImageView, num: Int?) {
        num?.let {
            val index = num + (DEFAULT_ until RANDOM_SIZE - 1).random()
            imageView.setImageResource(bubbleImageResources[index])
        }
    }

    @JvmStatic
    @BindingAdapter("Image")
    fun setImage(imageView: ImageView, uri: Uri?) {
        uri?.let {
            getScreenHeight(imageView.context)?.let { height ->
                imageView.maxHeight = height - 200
            }
            Glide.with(imageView)
                .load(uri)
                .error(R.drawable.ic_sara_normal).apply {
                    centerInside()
                }
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
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
        getScreenHeight(imageView.context)?.let { height ->
            imageView.maxHeight = height - 200
        }
        Glide.with(imageView)
            .load(url)
            .error(R.drawable.ic_sara_normal).apply {
                centerInside()
            }
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
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
            pxToDp(this, context).toInt() - 400
        }
    }
    private fun pxToDp(px: Int, context: Context) = px / ((context.resources.displayMetrics.densityDpi.toFloat()) / DisplayMetrics.DENSITY_DEFAULT)


    private val type1 = ColorStateList.valueOf(Color.parseColor("#3395f1"))
    private val type2 = ColorStateList.valueOf(Color.parseColor("#007bed"))
    private val type3 = ColorStateList.valueOf(Color.parseColor("#66b0f4"))
    private val type4 = ColorStateList.valueOf(Color.parseColor("#1d72c3"))
    @JvmStatic
    @BindingAdapter("TypeTint")
    fun setTypeTextBackgroundTint(textView: TextView, type: Int?) {
        type?.let {
            textView.backgroundTintList = when(type) {
                TYPE_1 -> type1
                TYPE_2 -> type2
                TYPE_3 -> type3
                else -> type4
            }
        }
    }

}