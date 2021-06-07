package com.watermelonman.weather.appbase.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog

open class BaseAlertDialog
@JvmOverloads
constructor(
    context: Context,
    @StyleRes themeResId: Int = 0
): AlertDialog(context, themeResId) {

    private var onPositiveButtonClick: (dialog: DialogInterface, which: Int) -> Unit = { _, _->}
    private var onNegativeButtonClick: (dialog: DialogInterface, which: Int) -> Unit = { _, _->}

    fun setMessage(@StringRes resId: Int) {
        setMessage(getString(resId))
    }

    fun setPositiveButton(@StringRes text: Int) {
        setButton(DialogInterface.BUTTON_POSITIVE, getString(text), ::positiveButtonListener)
    }

    fun setNegativeButton(@StringRes text: Int) {
        setButton(DialogInterface.BUTTON_NEGATIVE, getString(text), ::negativeButtonListener)
    }


    fun setPositiveButtonClickListener(listener: (dialog: DialogInterface, which: Int) -> Unit) {
        onPositiveButtonClick = listener
    }

    fun setNegativeButtonClickListener(listener: (dialog: DialogInterface, which: Int) -> Unit) {
        onNegativeButtonClick = listener
    }

    private fun positiveButtonListener(dialog: DialogInterface, which: Int) {
        onPositiveButtonClick(dialog, which)
    }

    private fun negativeButtonListener(dialog: DialogInterface, which: Int) {
        onNegativeButtonClick(dialog, which)
    }

    private fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

}