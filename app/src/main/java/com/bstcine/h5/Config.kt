package com.bstcine.h5

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


class Config {

    companion object {
        const val API_BASE_URL: String = "http://dev.bstcine.com/api"
        const val LEARN_URL: String = "http://dev.bstcine.com/learn"
        const val STORE_URL: String = "http://dev.bstcine.com"
        const val MINE_URL: String = "http://dev.bstcine.com/user"
    }

    private fun setProps(context: Context, p: Properties?) {
        var fos: FileOutputStream? = null
        try {
            val dirConf = context.filesDir
            val conf = File(dirConf, "config")
            fos = FileOutputStream(conf)

            p!!.store(fos, null)
            fos.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: Exception) {
            }

        }
    }

    operator fun get(context: Context): Properties? {
        var fis: FileInputStream? = null
        val props = Properties()
        try {
            val dirConf = context.filesDir
            fis = FileInputStream(dirConf.getPath() + File.separator
                    + "config")

            props.load(fis)
        } catch (e: Exception) {
        } finally {
            try {
                fis!!.close()
            } catch (e: Exception) {
            }

        }
        return props
    }

    operator fun get(context: Context, key: String): String? {
        val props = get(context)
        return props?.getProperty(key)
    }


    operator fun set(context: Context, ps: Properties) {
        val props = get(context)
        props!!.putAll(ps)
        setProps(context, props)
    }

    fun remove(context: Context, vararg key: String) {
        val props = get(context)
        for (k in key)
            props!!.remove(k)
        setProps(context, props)
    }

}