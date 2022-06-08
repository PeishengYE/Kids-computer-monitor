package com.radioyps.kidscomputermonitor

import android.util.Log
import android.widget.ImageView

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import androidx.core.net.toUri
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Log.v("BindingAdapter", "bindImage()>> started with Glide with URL: " + it)
        Glide.with(imgView.context)
            .load(imgUri)

            .apply(RequestOptions().timeout(6*1000)
//                        .placeholder(R.drawable.ic_broken_image)
                .error(R.drawable.ic_broken_image).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
            .into(imgView)
    }
}
