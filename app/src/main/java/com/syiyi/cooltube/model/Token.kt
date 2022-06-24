package com.syiyi.cooltube.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Token(
    var token: String? = null,
    var error: String? = null
)
