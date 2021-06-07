package com.watermelonman.weather.ui.fragment.selectlocation.dialog

import android.content.Context
import com.watermelonman.weather.R
import com.watermelonman.weather.appbase.dialog.BaseAlertDialog

class LocationPermissionIsRequiredDialog(
    context: Context
): BaseAlertDialog(context) {

    init {
        setMessage(R.string.location_required_dialog_message)
        setPositiveButton(R.string.open_settings)
        setNegativeButton(R.string.cancel)
    }
}