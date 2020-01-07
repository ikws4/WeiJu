package io.ikws4.weiju.adapters

import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("backgroundTint")
fun bindBackGroundTint(imageView: ImageView, @ColorRes tint: Int) {
    val context = imageView.context
    val color = ContextCompat.getColor(context, tint)
    imageView.setColorFilter(color)
}

@BindingAdapter("src")
fun bindSrc(view: ImageView, src: Int) {
    view.setImageResource(src)
}