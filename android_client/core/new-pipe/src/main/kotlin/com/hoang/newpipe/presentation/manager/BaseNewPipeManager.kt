package com.hoang.newpipe.presentation.manager

import com.hoang.newpipe.domain.entity.StreamInfo

interface BaseNewPipeManager {
    suspend fun searchSuggest(q: String): List<String>
    suspend fun search(q: String): List<StreamInfo>
}
