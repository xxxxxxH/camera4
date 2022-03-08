package com.sweetcam.app.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.sweetcam.app.R


object ResourceUtils {
    fun initStickersData():HashMap<String,Int>{
        val result = HashMap<String,Int>()
        result["1"] = R.mipmap.sticker11
        result["2"] = R.mipmap.sticker_101
        result["3"] = R.mipmap.sticker_201
        result["4"] = R.mipmap.sticker_301
        result["5"] = R.mipmap.sticker_401
        result["6"] = R.mipmap.sticker_501
        result["7"] = R.mipmap.sticker_601
        result["8"] = R.mipmap.sticker_701
        result["9"] = R.mipmap.sticker_801
        result["10"] = R.mipmap.sticker_901
        result["11"] = R.mipmap.sticker_1001
        result["12"] = R.mipmap.sticker_1101
        result["13"] = R.mipmap.sticker_1201
        result["14"] = R.mipmap.sticker_1301
        result["15"] = R.mipmap.sticker_1401
        result["16"] = R.mipmap.sticker_1501
        result["17"] = R.mipmap.sticker_1601
        result["18"] = R.mipmap.sticker_1701
        result["19"] = R.mipmap.sticker_1801
        result["20"] = R.mipmap.sticker_1901
        result["21"] = R.mipmap.sticker_2001
        result["22"] = R.mipmap.sticker_2101
        result["23"] = R.mipmap.sticker_2201
        result["24"] = R.mipmap.sticker_2301
        result["25"] = R.mipmap.sticker_2401
        result["26"] = R.mipmap.sticker_2501
        result["27"] = R.mipmap.sticker_2601
        return result
    }

    fun res2String(context: Context, id: Int): String {
        val r = context.resources
        val uri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + r.getResourcePackageName(id) + "/"
                    + r.getResourceTypeName(id) + "/"
                    + r.getResourceEntryName(id)
        )
        return uri.toString()
    }

    fun convertStringToIcon(str: String?): Bitmap? {
        // OutputStream out;
        var bitmap: Bitmap? = null
        return try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            val bitmapArray: ByteArray = Base64.decode(str, Base64.DEFAULT)
            bitmap = BitmapFactory.decodeByteArray(
                bitmapArray, 0,
                bitmapArray.size
            )
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}