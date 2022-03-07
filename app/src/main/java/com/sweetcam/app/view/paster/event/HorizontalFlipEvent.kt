package com.sweetcam.app.view.paster.event

import com.sweetcam.app.view.paster.PasterView

open class HorizontalFlipEvent : FlipEvent() {

    override val flipDirection: Int
        get() = PasterView.FLIP_HORIZONTALLY
}