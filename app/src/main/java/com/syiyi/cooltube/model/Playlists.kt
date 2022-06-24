package com.syiyi.cooltube.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Playlists(
    var id: String? = null,
    var name: String? = null,
    var shortDescription: String? = null,
    var thumbnail: String? = null,
)
