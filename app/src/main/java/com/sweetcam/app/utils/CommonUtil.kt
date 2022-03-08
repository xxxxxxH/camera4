package com.sweetcam.app.utils

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sweetcam.app.BuildConfig
import com.sweetcam.app.Constant
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.sweetcam.app.Ktx
import com.tencent.mmkv.MMKV
import java.io.File
import java.io.FileOutputStream


fun View.click(block: (View) -> Unit) {
    setOnClickListener {
        block(it)
    }
}


fun saveBitmap(name: String, bitmap: Bitmap) {
    val path =
        Environment.getExternalStorageDirectory().absolutePath + File.separator + name + ".jpg"

    FileOutputStream(File(path)).use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
        it.flush()
    }
}

fun AppCompatActivity.requestPermission(block: () -> Unit = {}) {
    XXPermissions.with(this)
        .permission(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
        .request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                if (all) {
                    block()
                } else {
                    Toast.makeText(
                        this@requestPermission,
                        "some permissions were not granted normally",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                super.onDenied(permissions, never)
                Toast.makeText(this@requestPermission, "no permissions", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
}

fun getResourceByFolder(
    clazz: Class<*>,
    folderName: String,
    filter: String
): ArrayList<com.sweetcam.app.pojo.ResourcePojo> {
    val result = ArrayList<com.sweetcam.app.pojo.ResourcePojo>()
    for (field in clazz.fields) {
        val name = field.name
        if (name.startsWith(filter)) {
            val id = app.resources.getIdentifier(name, folderName, app.packageName)
            val entity = com.sweetcam.app.pojo.ResourcePojo(name, id)
            result.add(entity)
        }
    }
    return result
}

fun ImageView.loadWith(any: Any) {
    Glide.with(this).load(any).into(this)
}

fun Context.jumpToWebByDefault(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url)).let {
    startActivity(it)
}

private val globalMetrics by lazy {
    Resources.getSystem().displayMetrics
}

val globalWidth by lazy {
    globalMetrics.widthPixels
}

val globalHeight by lazy {
    globalMetrics.heightPixels
}

var account
    get() = mmkv.getString(Constant.KEY_ACCOUNT, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_ACCOUNT, value)
    }

private var config
    get() = mmkv.getString(Constant.KEY_CONFIG, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_CONFIG, value)
    }

var configEntity
    get() = (config.ifBlank {
        "{}"
    }).let {
        gson.fromJson(it, com.sweetcam.app.pojo.ConfigPojo::class.java)
    }
    set(value) {
        config = gson.toJson(value)
    }

var adInvokeTime
    get() = mmkv.getInt(Constant.KEY_AD_INVOKE_TIME, 0)
    set(value) {
        mmkv.putInt(Constant.KEY_AD_INVOKE_TIME, value)
    }

var adRealTime
    get() = mmkv.getInt(Constant.KEY_AD_REAL_TIME, 0)
    set(value) {
        mmkv.putInt(Constant.KEY_AD_REAL_TIME, value)
    }

private var adShown
    get() = mmkv.getString(Constant.KEY_AD_SHOWN, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_AD_SHOWN, value)
    }

var adShownList
    get() = (adShown.ifBlank {
        "{}"
    }).let {
        gson.fromJson<List<Boolean>>(it, object : TypeToken<List<Boolean>>() {}.type)
    }
    set(value) {
        adShown = gson.toJson(value)
    }

var adShownIndex
    get() = mmkv.getInt(Constant.KEY_AD_SHOWN_INDEX, 0)
    set(value) {
        mmkv.putInt(Constant.KEY_AD_SHOWN_INDEX, value)
    }

var adLastTime
    get() = mmkv.getLong(Constant.KEY_AD_LAST_TIME, 0)
    set(value) {
        mmkv.putLong(Constant.KEY_AD_LAST_TIME, value)
    }

private var update
    get() = mmkv.getString(Constant.KEY_UPDATE, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_UPDATE, value)
    }

var updateEntity
    get() = (update.ifBlank {
        "{}"
    }).let {
        gson.fromJson(it, com.sweetcam.app.pojo.UpdatePojo::class.java)
    }
    set(value) {
        update = gson.toJson(value)
    }

var password
    get() = mmkv.getString(Constant.KEY_PASSWORD, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_PASSWORD, value)
    }

var isLogin
    get() = mmkv.getBoolean(Constant.KEY_IS_LOGIN, false)
    set(value) {
        mmkv.putBoolean(Constant.KEY_IS_LOGIN, value)
    }

val gson by lazy {
    Gson()
}

val mmkv by lazy {
    MMKV.defaultMMKV()
}

val app by lazy {
    Ktx.getInstance().app
}

fun <T> T.loge(tag: String = "defaultTag") {
    if (com.sweetcam.app.BuildConfig.DEBUG) {
        var content = toString()
        val segmentSize = 3 * 1024
        val length = content.length.toLong()
        if (length <= segmentSize) {
            Log.e(tag, content)
        } else {
            while (content.length > segmentSize) {
                val logContent = content.substring(0, segmentSize)
                content = content.replace(logContent, "")
                Log.e(tag, logContent)
            }
            Log.e(tag, content)
        }
    }
}

fun isInBackground(): Boolean {
    val activityManager = app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager
        .runningAppProcesses
    for (appProcess in appProcesses) {
        if (appProcess.processName == app.packageName) {
            return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }
    }
    return false
}