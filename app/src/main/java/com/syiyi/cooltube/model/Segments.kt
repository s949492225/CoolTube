package com.syiyi.cooltube.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.syiyi.cooltube.model.Segment

@JsonIgnoreProperties(ignoreUnknown = true)
data class Segments(
    val segments: MutableList<Segment> = arrayListOf()
) {
    constructor() : this(arrayListOf())
}
