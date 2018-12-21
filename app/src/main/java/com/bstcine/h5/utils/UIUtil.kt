package com.bstcine.h5.utils

import android.content.Context
import android.content.Intent
import com.bstcine.h5.home.BlankActivity

object UIUtil {

    fun toBlank(context: Context) {
        context.startActivity(Intent(context, BlankActivity::class.java))
    }

}