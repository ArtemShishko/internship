package com.devshish.internship.presentation.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.devshish.internship.R
import com.devshish.internship.presentation.ui.splash.SplashViewModel.SplashNavigationEvent.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            navigationEvent.observe(viewLifecycleOwner) {
                when (it) {
                    NAVIGATE_TO_AUTH -> {
                        val action = SplashFragmentDirections.actionSplashFragmentToNavGraphAuth()
                        findNavController().navigate(action)
                    }
                    NAVIGATE_TO_APP -> {
                        val action = SplashFragmentDirections.actionSplashFragmentToNavGraphHome()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }
}
