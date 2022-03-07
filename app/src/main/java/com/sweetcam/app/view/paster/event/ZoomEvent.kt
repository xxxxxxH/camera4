package com.sweetcam.app.view.paster.event

import com.sweetcam.app.view.paster.PasterView
import android.view.MotionEvent

class ZoomEvent : IEvent {

    override fun onActionDown(pasterView: PasterView?, event: MotionEvent?) {}
    override fun onActionMove(pasterView: PasterView?, event: MotionEvent?) {
        event?.let {
            pasterView?.zoomAndRotateCurrentSticker(it)
        }
    }

    override fun onActionUp(pasterView: PasterView?, event: MotionEvent?) {
        pasterView?.onStickerOperationListener?.let { listener->
            pasterView.currentSticker?.let {
                listener.onStickerZoomFinished(it)
            }
        }
    }
}