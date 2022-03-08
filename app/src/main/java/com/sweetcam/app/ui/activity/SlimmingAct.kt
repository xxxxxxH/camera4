package com.sweetcam.app.ui.activity

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.sweetcam.app.Constant
import com.sweetcam.app.R
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.MessageEvent
import com.sweetcam.app.utils.loadWith
import com.sweetcam.app.view.dialog.LoadingDialog
import com.sweetcam.app.view.dialog.SaveDialog
import kotlinx.android.synthetic.main.activity_slimming2.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@SuppressLint("ResourceType")
class SlimmingAct : BaseActivity(R.layout.activity_slimming2), View.OnClickListener {

    private val saveDialog by lazy{
        SaveDialog(this)
    }

    private val loadingDialog by lazy {
        LoadingDialog(this)
    }

    override fun onConvert() {
        EventBus.getDefault().register(this)
        slimmingMainIv.loadWith(Constant.imageUrl)
        createSlimmingItem()
        createWaistItem()
        createLegItem()
        createLengthItem()
        createBreastItem()
        createShoulderItem()
        cancel.setOnClickListener {
            finish()
        }
        save.setOnClickListener {
            saveDialog.show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        when (msg[0]) {
            "save" -> {
                saveDialog.dismiss()
                loadingDialog.show()
            }
        }
    }

    private fun createSlimmingItem() {
        val root = RelativeLayout(this)
        val rootLayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        rootLayoutParams.weight = 1F
        root.layoutParams = rootLayoutParams

        val textView = TextView(this)
        val textViewLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        textView.layoutParams = textViewLayoutParams
        textView.text = "slimming"
        textView.id = 1
        textView.textSize = 12f
        textView.setOnClickListener(this)
        setTvDrawable(textView, R.mipmap.slimming)
        root.addView(textView)
        option.addView(root)
    }

    private fun createWaistItem() {
        val root = RelativeLayout(this)
        val rootLayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        rootLayoutParams.weight = 1F
        root.layoutParams = rootLayoutParams

        val textView = TextView(this)
        val textViewLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        textView.layoutParams = textViewLayoutParams
        textView.text = "waist"
        textView.id = 2
        textView.textSize = 12f
        textView.setOnClickListener(this)
        setTvDrawable(textView, R.mipmap.waist)
        root.addView(textView)
        option.addView(root)
    }

    private fun createLegItem() {
        val root = RelativeLayout(this)
        val rootLayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        rootLayoutParams.weight = 1F
        root.layoutParams = rootLayoutParams

        val textView = TextView(this)
        val textViewLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        textView.layoutParams = textViewLayoutParams
        textView.text = "legs"
        textView.id = 3
        textView.textSize = 12f
        textView.setOnClickListener(this)
        setTvDrawable(textView, R.mipmap.legs)
        root.addView(textView)
        option.addView(root)
    }

    private fun createLengthItem() {
        val root = RelativeLayout(this)
        val rootLayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        rootLayoutParams.weight = 1F
        root.layoutParams = rootLayoutParams

        val textView = TextView(this)
        val textViewLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        textView.layoutParams = textViewLayoutParams
        textView.text = "legs length"
        textView.id = 4
        textView.textSize = 12f
        textView.setOnClickListener(this)
        setTvDrawable(textView, R.mipmap.legs_length)
        root.addView(textView)
        option.addView(root)
    }


    private fun createBreastItem() {
        val root = RelativeLayout(this)
        val rootLayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        rootLayoutParams.weight = 1F
        root.layoutParams = rootLayoutParams

        val textView = TextView(this)
        val textViewLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        textView.layoutParams = textViewLayoutParams
        textView.text = "breast"
        textView.id = 5
        textView.textSize = 12f
        textView.setOnClickListener(this)
        setTvDrawable(textView, R.mipmap.breast)
        root.addView(textView)
        option.addView(root)
    }

    private fun createShoulderItem() {
        val root = RelativeLayout(this)
        val rootLayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        rootLayoutParams.weight = 1F
        root.layoutParams = rootLayoutParams

        val textView = TextView(this)
        val textViewLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        textView.layoutParams = textViewLayoutParams
        textView.text = "shoulder"
        textView.id = 6
        textView.textSize = 12f
        textView.setOnClickListener(this)
        setTvDrawable(textView, R.mipmap.shoulder)
        root.addView(textView)
        option.addView(root)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setTvDrawable(tv: TextView, id: Int) {
        val d = resources.getDrawable(id)
        d.setBounds(0, 0, d.minimumWidth, d.minimumHeight)
        tv.setCompoundDrawables(null, d, null, null)
    }


    override fun onClick(p0: View?) {
        val v = p0 as TextView
        when (v.text) {
            "slimming" -> {
                setTvDrawable(findViewById<TextView>(1), R.mipmap.slimming_c)
                setTvDrawable(findViewById<TextView>(2), R.mipmap.waist)
                setTvDrawable(findViewById<TextView>(3), R.mipmap.legs)
                setTvDrawable(findViewById<TextView>(4), R.mipmap.legs_length)
                setTvDrawable(findViewById<TextView>(5), R.mipmap.breast)
                setTvDrawable(findViewById<TextView>(6), R.mipmap.shoulder)
            }
            "waist" -> {
                setTvDrawable(findViewById<TextView>(1), R.mipmap.slimming)
                setTvDrawable(findViewById<TextView>(2), R.mipmap.waist_c)
                setTvDrawable(findViewById<TextView>(3), R.mipmap.legs)
                setTvDrawable(findViewById<TextView>(4), R.mipmap.legs_length)
                setTvDrawable(findViewById<TextView>(5), R.mipmap.breast)
                setTvDrawable(findViewById<TextView>(6), R.mipmap.shoulder)
            }
            "legs" -> {
                setTvDrawable(findViewById<TextView>(1), R.mipmap.slimming)
                setTvDrawable(findViewById<TextView>(2), R.mipmap.waist)
                setTvDrawable(findViewById<TextView>(3), R.mipmap.legs_c)
                setTvDrawable(findViewById<TextView>(4), R.mipmap.legs_length)
                setTvDrawable(findViewById<TextView>(5), R.mipmap.breast)
                setTvDrawable(findViewById<TextView>(6), R.mipmap.shoulder)
            }
            "legs length" -> {
                setTvDrawable(findViewById<TextView>(1), R.mipmap.slimming)
                setTvDrawable(findViewById<TextView>(2), R.mipmap.waist)
                setTvDrawable(findViewById<TextView>(3), R.mipmap.legs)
                setTvDrawable(findViewById<TextView>(4), R.mipmap.leg_l_c)
                setTvDrawable(findViewById<TextView>(5), R.mipmap.breast)
                setTvDrawable(findViewById<TextView>(6), R.mipmap.shoulder)
            }
            "breast" -> {
                setTvDrawable(findViewById<TextView>(1), R.mipmap.slimming)
                setTvDrawable(findViewById<TextView>(2), R.mipmap.waist)
                setTvDrawable(findViewById<TextView>(3), R.mipmap.legs)
                setTvDrawable(findViewById<TextView>(4), R.mipmap.legs_length)
                setTvDrawable(findViewById<TextView>(5), R.mipmap.breast_c)
                setTvDrawable(findViewById<TextView>(6), R.mipmap.shoulder)
            }
            "shoulder" -> {
                setTvDrawable(findViewById<TextView>(1), R.mipmap.slimming)
                setTvDrawable(findViewById<TextView>(2), R.mipmap.waist)
                setTvDrawable(findViewById<TextView>(3), R.mipmap.legs)
                setTvDrawable(findViewById<TextView>(4), R.mipmap.legs_length)
                setTvDrawable(findViewById<TextView>(5), R.mipmap.breast)
                setTvDrawable(findViewById<TextView>(6), R.mipmap.shoulder_c)
            }
        }
    }
}