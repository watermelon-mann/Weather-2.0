package com.watermelonman.data.utils


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

var isNetworkAvailable = false
private set

class ConnectionStateManager
private  constructor(
    private val context: Context,
    private val owner: LifecycleOwner
) : ConnectivityManager.NetworkCallback() {

    private var connectivityManager: ConnectivityManager? = null
    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    init {
        enable()
    }

    override fun onAvailable(network: Network) {
        isNetworkAvailable = true
        super.onAvailable(network)
    }

    override fun onLost(network: Network) {
        isNetworkAvailable = false
        super.onLost(network)
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        isNetworkAvailable = true
        super.onCapabilitiesChanged(network, networkCapabilities)

    }

    private fun enable() {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager!!.registerNetworkCallback(networkRequest, this)
        owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                disable()
                owner.lifecycle.removeObserver(this)
            }
        })
    }

    private fun disable() {
        connectivityManager?.unregisterNetworkCallback(this)
    }

    companion object {
        fun bindToActivity(activity: AppCompatActivity) {
            ConnectionStateManager(activity, activity)
        }
    }
}