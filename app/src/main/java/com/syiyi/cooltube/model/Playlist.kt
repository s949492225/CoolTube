package com.syiyi.cooltube.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Playlist(
    var name: String? = null,
    var thumbnailUrl: String? = null,
    var bannerUrl: String? = null,
    var nextpage: String? = null,
    var uploader: String? = null,
    var uploaderUrl: String? = null,
    var uploaderAvatar: String? = null,
    var videos: Int? = 0,
    var relatedStreams: List<StreamItem>? = null,
)
