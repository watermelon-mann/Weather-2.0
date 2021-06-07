package com.watermelonman.weather.ui.fragment.selectlocation.dialog

import android.content.Context
import com.watermelonman.weather.R
import com.watermelonman.weather.appbase.dialog.BaseAlertDialog

class LocationServiceUnavailableDialog(
    context: Context
): BaseAlertDialog(context) {

    init {
        setMessage(R.string.location_is_unavailable_dialog_message)
        setPositiveButton(R.string.ok)
    }
}