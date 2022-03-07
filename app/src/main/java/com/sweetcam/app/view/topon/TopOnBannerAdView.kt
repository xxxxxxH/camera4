package com.sweetcam.app.view.topon

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.anythink.banner.api.ATBannerExListener
import com.anythink.banner.api.ATBannerView
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.nativead.banner.api.ATNativeBannerConfig
import com.anythink.nativead.banner.api.ATNativeBannerListener
import com.anythink.nativead.banner.api.ATNativeBannerSize
import com.anythink.nativead.banner.api.ATNativeBannerView
import com.sweetcam.app.R
import com.sweetcam.app.utils.app
import com.sweetcam.app.utils.loge

class TopOnBannerAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        val bannerView by lazy {
            ATBannerView(app)
        }
    }

    init {
        addBanner()
    }

    private fun addBanner() {
        (bannerView.parent as? ViewGroup)?.removeView(bannerView)
        addView(
            bannerView,
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        bannerView.apply {
            setPlacementId(app.getString(R.string.top_on_banner_ad_id))
            setBannerAdListener(object : ATBannerExListener {
                override fun onBannerLoaded() {
                    "TopOnBannerAdView onBannerLoaded".loge()
                }

                override fun onBannerFailed(p0: AdError?) {
                    "TopOnBannerAdView onBannerFailed $p0".loge()
                }

                override fun onBannerClicked(p0: ATAdInfo?) {
                    "TopOnBannerAdView onBannerClicked $p0".loge()
                }

                override fun onBannerShow(p0: ATAdInfo?) {
                    "TopOnBannerAdView onBannerShow $p0".loge()
                }

                override fun onBannerClose(p0: ATAdInfo?) {
                    "TopOnBannerAdView onBannerClose $p0".loge()
                }

                override fun onBannerAutoRefreshed(p0: ATAdInfo?) {
                    "TopOnBannerAdView onBannerAutoRefreshed $p0".loge()
                }

                override fun onBannerAutoRefreshFail(p0: AdError?) {
                    "TopOnBannerAdView onBannerAutoRefreshFail $p0".loge()
                }

                override fun onDeeplinkCallback(p0: Boolean, p1: ATAdInfo?, p2: Boolean) {
                    "TopOnBannerAdView onDeeplinkCallback $p0".loge()
                }
            })
        }
        bannerView.loadAd()
    }
}