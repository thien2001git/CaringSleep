package com.hoang.newpipe.presentation.manager

import com.hoang.newpipe.presentation.manager.helper.DownloaderImpl
import org.schabi.newpipe.extractor.NewPipe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewPipeManager @Inject constructor(
    downloader: DownloaderImpl,
): BaseNewPipeManager {
    init {
        NewPipe.init(downloader)
    }
    override suspend fun searchSuggest(q: String): List<String> {
        TODO("Not yet implemented")
    }

}