package com.devshish.internship.presentation.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.devshish.internship.R
import com.devshish.internship.data.repository.ProfileRepository
import com.devshish.internship.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by lazy {
        val repository = ProfileRepository()
        val factory = ProfileViewModelFactory(repository)
        ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentProfileBinding.bind(view)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apply {
                tvNickname.text = user.nickname
                tvCountry.text = user.country
                tvCity.text = user.city
                tvDescription.text = user.description
                tvBackground.text = user.background

                Glide.with(this@ProfileFragment)
                    .load(user.photo)
                    .placeholder(R.drawable.liked)
                    .into(ivProfilePicture)
            }
        }
    }
}
