package com.hoang.newpipe.presentation.manager.service

import com.hoang.newpipe.domain.entity.StreamInfo
import com.hoang.newpipe.presentation.manager.BaseNewPipeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.StreamingService
import org.schabi.newpipe.extractor.InfoItem as ExtractorInfo

abstract class BaseNewPipeService<T: StreamingService>: BaseNewPipeManager {
    abstract val service: T

    override suspend fun searchSuggest(q: String): List<String> = withContext(Dispatchers.IO) {
        service.suggestionExtractor.suggestionList(q)
    }

    override suspend fun search(q: String): List<StreamInfo> = withContext(Dispatchers.IO) {
        service.getSearchExtractor(q).let {
            it.fetchPage()
            it.initialPage.items
        }.map {
            it.toDomain()
        }
    }

    private fun ExtractorInfo.toDomain(): StreamInfo {
        TODO("Not yet implemented")
    }
}
