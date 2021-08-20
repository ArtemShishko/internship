package com.devshish.internship.presentation.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.devshish.internship.R
import com.devshish.internship.data.repository.ProfileRepository
import com.devshish.internship.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by lazy {
        val repository = ProfileRepository
        val factory = ProfileViewModelFactory(repository)
        ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentProfileBinding.bind(view)

        with(viewModel) {
            with(binding) {
                viewLifecycleOwner.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        user.collect { user ->
                            Glide.with(this@ProfileFragment)
                                .load(user.photo)
                                .placeholder(R.drawable.ic_profile)
                                .into(ivProfilePicture)

                            tvNickname.text = user.nickname
                            tvDescription.checkAndSetText(
                                user.description,
                                R.string.profile_user_description
                            )
                            tvCountry.checkAndSetText(user.country, R.string.profile_user_country)
                            tvCity.checkAndSetText(user.city, R.string.profile_user_city)
                            tvBackground.checkAndSetText(user.background, R.string.profile_user_background)
                        }
                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        navigateForwardEvent.collect {
                            val action = ProfileFragmentDirections
                                .actionProfileFragmentToEditProfileFragment()
                            findNavController().navigate(action)
                        }
                    }
                }

                ivEditProfile.setOnClickListener {
                    onEditButtonClick(
                        nickname = tvNickname.text.toString(),
                        country = tvCountry.text.toString(),
                        city = tvCity.text.toString(),
                        description = tvDescription.text.toString()
                    )
                }
            }
        }
    }

    private fun TextView.checkAndSetText(data: String?, resourceId: Int) {
        val checkedData = data ?: getString(R.string.data_not_chosen)
        text = getString(resourceId, checkedData)
    }
}
