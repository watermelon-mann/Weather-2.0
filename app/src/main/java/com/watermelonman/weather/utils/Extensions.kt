package com.watermelonman.weather.utils

import android.app.Dialog
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.watermelonman.entities.result.CallException
import com.watermelonman.entities.result.ErrorCode
import com.watermelonman.entities.result.Result
import com.watermelonman.entities.result.State
import com.watermelonman.weather.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

//-------------------  View  -------------------

fun View.fadeOutIfVisible(visibility: Int = View.GONE, root: ViewGroup = this.parent as ViewGroup) {
    TransitionManager.beginDelayedTransition(root)
    this.visibility = visibility
}

fun View.fadeInIfInGone(root: ViewGroup = this.parent as ViewGroup) {
    TransitionManager.beginDelayedTransition(root)
    isVisible = true
}

fun View.showShortSnackBar(@StringRes message: Int, @StringRes actionTextRes: Int? = null, action: ((View) -> Unit) = {}) =
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply {
        actionTextRes?.let { setAction(it, action) }
        show()
    }


fun View.showIndefiniteSnackBar(@StringRes message: Int, @StringRes actionTextRes: Int? = null, action: ((View) -> Unit) = {}) =
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE).apply {
        actionTextRes?.let { setAction(it, action) }
        show()
    }


//-------------------  Dialog  -------------------

fun Dialog.makeFullScreen(
    root: View,
    fragment: Fragment,
    wrapperSize: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    peekHeightPercent: Float = 0.97f
) {
    val bottomSheet = findViewById<View>(R.id.design_bottom_sheet)
    bottomSheet?.layoutParams?.height = wrapperSize
    val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
    root.viewTreeObserver?.addOnGlobalLayoutListener(object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            root.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            behavior.peekHeight = (root.height * peekHeightPercent).toInt()
            fragment.view?.requestLayout()
        }
    })
}

//-------------------  Fragment  -------------------

fun Fragment.permissionRequestLauncher(block: (isGranted: Boolean) -> Unit) =
    registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        block(it)
    }

inline fun<reified T> Fragment.collectWhenStarted(flow: Flow<T>, noinline block: (value: T) -> Unit) {
    flow.observeOnLifecycle(viewLifecycleOwner) {
        withContext(Dispatchers.Main) { block(it) }
    }
}

fun Fragment.isFragmentInBackStack(destinationId: Int) =
    findNavController().isFragmentInBackStack(destinationId)

//-------------------  NavController  -------------------

fun NavController.isFragmentInBackStack(destinationId: Int) =
    try {
        getBackStackEntry(destinationId)
        true
    } catch (e: Exception) {
        false
    }

//-------------------  Context  -------------------

fun View.hideKeyboard() {
    context ?: return
    val imm = ContextCompat.getSystemService(context, InputMethodManager::class.java)
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

//-------------------  Other  -------------------

suspend fun<T> Result<T>.processResult(
    onSuccess: suspend (data: T) -> Unit,
    onError: suspend (callException: CallException) -> Unit = {}) {
    when(this) {
        is Result.Success -> onSuccess(data)
        is Result.Error -> onError(error)
    }
}

fun<T> State<T>.precessState(
    onSuccess: (T) -> Unit,
    onLoading: () -> Unit = {},
    onError: (ErrorCode) -> Unit = {}
) {
    when(this) {
        is State.Loading -> onLoading()
        is State.Success -> onSuccess(data)
        is State.Error -> onError(errorCode)
    }
}