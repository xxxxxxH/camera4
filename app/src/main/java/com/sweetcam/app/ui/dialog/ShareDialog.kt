package com.sweetcam.app.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.sweetcam.app.R
import com.sweetcam.app.base.BaseDialog
import com.sweetcam.app.utils.app
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShareDialog : BaseDialog() {

    companion object {
        fun newInstance() = ShareDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LayoutInflater.from(app).inflate(R.layout.dialog_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            delay(10000)
            withContext(Dispatchers.Main) {
                dismiss()
            }
        }
    }
}