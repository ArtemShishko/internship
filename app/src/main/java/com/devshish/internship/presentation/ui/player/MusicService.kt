package com.devshish.internship.presentation.ui.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.devshish.internship.data.repository.LocalSongsRepository
import com.devshish.internship.domain.repository.ISongsRepository
import com.devshish.internship.presentation.ui.utils.Constants.MEDIA_ROOT_ID
import com.devshish.internship.presentation.ui.utils.Constants.NETWORK_ERROR
import com.devshish.internship.presentation.ui.utils.Constants.SERVICE_TAG
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class MusicService : MediaBrowserServiceCompat() {

    private val dataSourceFactory: DataSource.Factory by inject()

    private val exoPlayer: ExoPlayer by inject()

    private val musicDatabase: ISongsRepository by inject()
    var localMusicSource: LocalMusicSource = LocalMusicSource(musicDatabase)

    private lateinit var musicNotificationManager: MusicNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false

    private var curPlayingSong: MediaMetadataCompat? = null

    private var isPlayerInitialized = false

    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    companion object {
        var curSongDuration = 0L
            private set
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            localMusicSource.fetchMediaData()
        }

        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this)
        ) {
            curSongDuration = exoPlayer.duration
        }

        val musicPlaybackPreparer = MusicPlaybackPreparer(localMusicSource) {
            curPlayingSong = it
            preparerPlayer(
                localMusicSource.songs,
                it,
                true
            )
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)

        musicPlayerEventListener = MusicPlayerEventListener(this)

        exoPlayer.addListener(musicPlayerEventListener)
        musicNotificationManager.showNotification(exoPlayer)
    }

    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {

        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat =
            localMusicSource.songs[windowIndex].description
    }

    private fun preparerPlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ) {
        val curSongIndex = if (curPlayingSong == null) 0 else songs.indexOf(itemToPlay)
        exoPlayer.prepare(localMusicSource.asMediaSource(dataSourceFactory))
        exoPlayer.seekTo(curSongIndex, 0L)
        exoPlayer.playWhenReady = playNow
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()

        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? = BrowserRoot(MEDIA_ROOT_ID, null)

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            MEDIA_ROOT_ID -> {
                val resultsSent = localMusicSource.whenReady { isInitialized ->
                    if (isInitialized) {
                        result.sendResult(localMusicSource.asMediaItems())
                        if (!isPlayerInitialized && localMusicSource.songs.isNotEmpty()) {
                            preparerPlayer(
                                localMusicSource.songs,
                                localMusicSource.songs[0],
                                false
                            )
                            isPlayerInitialized = true
                        }
                    } else {
                        mediaSession.sendSessionEvent(NETWORK_ERROR, null)
                        result.sendResult(null)
                    }
                }
                if (!resultsSent) {
                    result.detach()
                }
            }
        }
    }
}
