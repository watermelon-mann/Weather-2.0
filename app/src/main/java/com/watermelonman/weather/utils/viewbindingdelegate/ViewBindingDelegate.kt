package com.watermelonman.weather.utils.viewbindingdelegate


import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding


class ViewBindingDelegate<F : Fragment, T : ViewBinding>(
    viewBinder: (F) -> T
) : ViewBindingProperty<F, T>(viewBinder) {
    override fun getLifecycleOwner(thisRef: F) = thisRef.viewLifecycleOwner
}



inline fun <reified T : ViewBinding> Fragment.viewBinding(): ViewBindingProperty<Fragment, T> =
    viewBinding(FragmentInflateViewBinder(T::class.java)::bind)

fun <F : Fragment, T : ViewBinding> Fragment.viewBinding(viewBinder: (F) -> T): ViewBindingProperty<F, T> {
    return ViewBindingDelegate(viewBinder)
}