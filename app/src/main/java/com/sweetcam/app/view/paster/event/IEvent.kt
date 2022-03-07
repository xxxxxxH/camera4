package com.sweetcam.app.view.paster.event

import com.sweetcam.app.view.paster.PasterView
import android.view.MotionEvent

interface IEvent {
    fun onActionDown(pasterView: PasterView?, event: MotionEvent?)
    fun onActionMove(pasterView: PasterView?, event: MotionEvent?)
    fun onActionUp(pasterView: PasterView?, event: MotionEvent?)
}