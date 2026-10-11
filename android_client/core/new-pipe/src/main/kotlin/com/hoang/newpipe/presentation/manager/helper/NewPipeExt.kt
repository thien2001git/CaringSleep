package com.hoang.newpipe.presentation.manager.helper

import org.schabi.newpipe.extractor.Info
import org.schabi.newpipe.extractor.ServiceList
import java.util.concurrent.TimeUnit

fun Info.getCacheExpirationMillis() =
    if (serviceId == ServiceList.SoundCloud.serviceId)
        TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)
    else
        TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)