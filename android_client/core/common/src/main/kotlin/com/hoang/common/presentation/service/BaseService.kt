package com.hoang.common.presentation.service

import androidx.lifecycle.LifecycleService

abstract class BaseService :
    LifecycleService(),
    IsForeground {
    abstract val notificationId: Int

    override fun onCreate() {
        super.onCreate()
        if (isForeground) {
            // todo start notification
        }
    }
}
