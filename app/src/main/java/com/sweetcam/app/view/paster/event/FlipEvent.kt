package com.sweetcam.app.view.paster.event

import com.sweetcam.app.view.paster.PasterView
import android.view.MotionEvent

abstract class FlipEvent : IEvent {
    override fun onActionDown(pasterView: PasterView?, event: MotionEvent?) {}
    override fun onActionMove(pasterView: PasterView?, event: MotionEvent?) {}
    override fun onActionUp(pasterView: PasterView?, event: MotionEvent?) {
        pasterView?.flipCurrentSticker(flipDirection)
    }

    protected abstract val flipDirection: Int
}