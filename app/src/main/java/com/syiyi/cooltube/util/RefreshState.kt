package com.syiyi.cooltube.util

enum class RefreshState {
    INIT,
    LOCAL_EMPTY,
    LOCAL_SUCCESS,
    REFRESH,
    PULL_REFRESH,
    PULL_ERROR,
    ERROR,
    EMPTY,
    SUCCESS,
    LOAD_MORE,
    LOAD_MORE_EMPTY,
    LOAD_MORE_ERROR,
}