package com.sweetcam.app.view.paster.event

import com.sweetcam.app.view.paster.PasterView
import android.view.MotionEvent

class DeleteEvent : IEvent {
    override fun onActionDown(pasterView: PasterView?, event: MotionEvent?) {}
    override fun onActionMove(pasterView: PasterView?, event: MotionEvent?) {}
    override fun onActionUp(pasterView: PasterView?, event: MotionEvent?) {
        pasterView?.removeCurrentSticker()
    }
}