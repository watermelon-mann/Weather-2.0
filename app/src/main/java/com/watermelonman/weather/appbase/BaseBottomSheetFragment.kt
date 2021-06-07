package com.watermelonman.weather.appbase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.watermelonman.weather.utils.collectWhenStarted
import com.watermelonman.weather.utils.navigation.Command
import com.watermelonman.weather.utils.navigation.ViewCommand

abstract class BaseBottomSheetFragment<T : BaseViewModel, V : ViewBinding> :
    BottomSheetDialogFragment() {

    protected abstract val viewModel: T
    protected abstract val binding: V
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this) {
            navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        collectWhenStarted(viewModel.command) { processCommand(it) }
        observes()
        initView(savedInstanceState)
    }

    protected open fun initView(savedInstanceState: Bundle?) {}
    protected open fun initData(savedInstanceState: Bundle?) {}
    protected open fun observes() {}

    protected open fun navigateUp() {
        navController.popBackStack()
    }

    protected open fun processCommand(command: ViewCommand) {
        if (command is Command.FinishAppCommand) {
            activity?.finish()
        } else if (command is Command.NavCommand) {
            navController.navigate(command.navDirections)
        }
    }
}