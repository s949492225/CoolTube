package com.syiyi.cooltube.util

import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.syiyi.cooltube.model.PipedStream
import com.syiyi.cooltube.model.Streams

fun Streams.mediaSources(): List<Pair<String, MediaSource>> {

    val videoStreams = videoStreams!!.filter {
        if (it.format.equals("WEBM")) { // preferred format
            true
        } else it.quality.equals("LBRY") && it.format.equals("MP4")
    }

    // create a list of subtitles
    val subtitle = mutableListOf<MediaItem.SubtitleConfiguration>()

    subtitles!!.forEach {
        subtitle.add(
            MediaItem.SubtitleConfiguration.Builder(it.url!!.toUri())
                .setMimeType(it.mimeType!!) // The correct MIME type (required).
                .setLanguage(it.code) // The subtitle language (optional).
                .build()
        )
    }

    fun getMostBitRate(audios: List<PipedStream>): String {
        var bitrate = 0
        var index = 0
        for ((i, audio) in audios.withIndex()) {
            val q = audio.quality!!.replace(" kbps", "").toInt()
            if (q > bitrate) {
                bitrate = q
                index = i
            }
        }
        return audios[index].url!!
    }

    val audioUrl = getMostBitRate(audioStreams!!)


    val dataSourceFactory: DataSource.Factory =
        DefaultHttpDataSource.Factory()


    val audioSource: MediaSource =
        ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(audioUrl))

    val ret = videoStreams
        .reversed()
        .map {
            val videoItem: MediaItem = MediaItem.Builder()
                .setUri(it.url!!.toUri())
                .setSubtitleConfigurations(subtitle)
                .build()

            val videoSource: MediaSource =
                DefaultMediaSourceFactory(dataSourceFactory)
                    .createMediaSource(videoItem)

            Pair(it.quality ?: "未知", MergingMediaSource(videoSource, audioSource))
        }
    return ret
}