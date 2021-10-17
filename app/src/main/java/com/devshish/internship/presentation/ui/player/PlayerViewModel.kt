package com.devshish.internship.presentation.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devshish.internship.domain.model.Song
import com.devshish.internship.domain.repository.ISongsRepository
import com.devshish.internship.presentation.ui.service.client.MediaBrowserClient

class PlayerViewModel(
    repository: ISongsRepository,
    private val mediaBrowser: MediaBrowserClient
) : ViewModel() {

    val songToPlay: LiveData<Song>
        get() = _songToPlay
    private val _songToPlay = MutableLiveData<Song>()

    init {
        _songToPlay.value = repository.songToPlay
        mediaBrowser.playFromUri(repository.songToPlay?.uri)
    }

    fun toggle() = mediaBrowser.toggle()
}
