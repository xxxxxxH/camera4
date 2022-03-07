package com.sweetcam.app.ui.activity

import android.content.Intent
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.sweetcam.app.*
import com.sweetcam.app.R
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.*
import kotlinx.android.synthetic.main.activity_face_book.*
import kotlinx.android.synthetic.main.activity_splash.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class IndexActivity : BaseActivity(R.layout.activity_splash) {

    override fun onConvert() {
        registerEventBus()
        lifecycleScope.requestConfig {
            if (isLogin) {
                jumpToMain()
            } else {
                if (configEntity.needLogin()) {
                    if (configEntity.needDeepLink() && configEntity.faceBookId().isNotBlank()) {
                        fetchAppLink(configEntity.faceBookId()) {
                            "initFaceBook $it".loge()
                            it?.let {
                                runOnUiThread {
                                    activitySplashIvFb.isVisible = true
                                }
                            } ?: kotlin.run {
                                jumpToMain()
                            }
                        }
                    } else {
                        activitySplashIvFb.isVisible = true
                    }
                } else {
                    jumpToMain()
                }
            }
        }

        activitySplashIvFb.click {
            startActivity(Intent(this@IndexActivity, LoginActivity::class.java))
        }
    }

    private fun jumpToMain() {
        startActivity(Intent(this@IndexActivity, MainActivity::class.java))
        finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: BusDestroyEvent) {
        finish()
    }

    override fun onPause() {
        super.onPause()
        canShowAd = false
    }

    private var canShowAd = true

    override fun onInterstitialAdLoaded() {
        if (configEntity.isOpenAdReplacedByInsertAd()) {
            if (canShowAd) {
                showInsertAd(isForce = true)
                canShowAd = false
            }
        }
    }

    override fun onSplashAdLoaded() {
        if (!configEntity.isOpenAdReplacedByInsertAd()) {
            if (canShowAd) {
                canShowAd = false
                showOpenAdImpl(activitySplashRl, "")
            }
        }
    }

}