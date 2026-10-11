package com.hoang.newpipe.presentation.manager.service

import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.services.youtube.YoutubeService

class YoutubeService: BaseNewPipeService<YoutubeService>() {
    override val service: YoutubeService = ServiceList.YouTube
}