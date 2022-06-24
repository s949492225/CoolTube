package com.syiyi.cooltube.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Login(
    val username: String? = null,
    val password: String? = null
)
