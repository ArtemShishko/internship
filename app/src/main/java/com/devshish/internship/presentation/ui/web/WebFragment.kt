package com.devshish.internship.presentation.ui.web

import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.devshish.internship.R
import com.devshish.internship.databinding.FragmentWebBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WebFragment : Fragment(R.layout.fragment_web) {

    private val binding by viewBinding(FragmentWebBinding::bind)
    private val viewModel: WebViewModel by viewModel()

    private val args: WebFragmentArgs by navArgs()

    private val client = GeniusAuthWebViewClient {
        viewModel.inputCode(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.apply {
            setTitle(R.string.auth_sign_in_with_genius)
            setHomeAsUpIndicator(R.drawable.ic_close)
            setDisplayHomeAsUpEnabled(true)
        }

        setupWebView()

        with(viewModel) {
            navigateForward.observe(viewLifecycleOwner) {
                val action = WebFragmentDirections.actionWebFragmentToNavGraphHome()
                findNavController().navigate(action)
            }
        }
    }

    private fun setupWebView() {
        binding.webView.apply {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()

            settings.javaScriptEnabled = true
            webViewClient = client
            loadUrl(args.link)
        }
    }
}
