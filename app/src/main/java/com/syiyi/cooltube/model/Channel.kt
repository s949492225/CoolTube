package com.syiyi.cooltube.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Channel(
    var id: String? = null,
    var name: String? = null,
    var avatarUrl: String? = null,
    var bannerUrl: String? = null,
    var description: String? = null,
    var nextpage: String? = null,
    var subscriberCount: Long = 0,
    var verified: Boolean = false,
    var relatedStreams: List<StreamItem>? = null
)
