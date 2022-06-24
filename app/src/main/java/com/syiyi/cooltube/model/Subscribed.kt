package com.syiyi.cooltube.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Subscribed(
    var subscribed: Boolean? = null
)
