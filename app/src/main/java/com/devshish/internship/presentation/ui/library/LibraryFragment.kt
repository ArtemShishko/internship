package com.devshish.internship.presentation.ui.library

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.devshish.internship.R
import com.devshish.internship.databinding.FragmentLibraryBinding
import com.devshish.internship.databinding.ItemPlaylistBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class LibraryFragment : Fragment(R.layout.fragment_library) {

    private val binding by viewBinding(FragmentLibraryBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            layoutSavedSongs.setData(
                playlistCover = R.drawable.lyrics,
                playlistTitle = R.string.library_saved_lyrics_title,
                playlistItemSubTitle = R.string.library_saved_lyrics_subtitle
            ) {
                val action = LibraryFragmentDirections.actionLibraryFragmentToSavedLyricsFragment()
                findNavController().navigate(action)
            }

            layoutLocalSongs.setData(
                playlistCover = R.drawable.album,
                playlistTitle = R.string.library_local_songs_title,
                playlistItemSubTitle = R.string.library_local_songs_subtitle
            ) {
                val action = LibraryFragmentDirections.actionLibraryFragmentToLocalSongsFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun ItemPlaylistBinding.setData(
        @DrawableRes playlistCover: Int?,
        @StringRes playlistTitle: Int,
        @StringRes playlistItemSubTitle: Int,
        navigateToPlaylist: () -> Unit
    ) {
        Glide.with(this@LibraryFragment)
            .load(playlistCover)
            .placeholder(R.color.purple_200)
            .into(ivPlaylistCover)
        tvPlaylistTitle.text = getString(playlistTitle)
        tvPlaylistSubTitle.text = getString(playlistItemSubTitle)
        root.setOnClickListener { navigateToPlaylist() }
    }
}
