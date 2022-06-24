package com.syiyi.cooltube.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.syiyi.cooltube.model.SearchItem

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchResult(
    val items: MutableList<SearchItem>? = arrayListOf(),
    val nextpage: String? = "",
    val suggestion: String? = "",
    val corrected: Boolean? = null
)
