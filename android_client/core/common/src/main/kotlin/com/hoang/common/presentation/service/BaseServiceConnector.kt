package com.hoang.common.presentation.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.hoang.common.utils.AppLog

abstract class BaseServiceConnector<T : BaseService>(
    private val context: Context,
    private val serviceClass: Class<T>,
) : ServiceConnection,
    IsForeground {
    // Exposed so subclasses can interact with the bound service
    protected var service: T? = null
        private set

    init {
        connect()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onServiceConnected(
        name: ComponentName?,
        binder: IBinder?,
    ) {
        val localBinder = binder as? BaseServiceBinder<*>
        service = localBinder?.service as? T
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }

    fun connect() {
        val intent = Intent(context, serviceClass)

        // 1. Start the service (keeps it running independently of UI)
        if (isForeground) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        // 2. Bind to the service (allows method calls from connector to service)
        context.bindService(
            intent,
            this,
            Context.BIND_AUTO_CREATE,
        )
    }

    fun disconnect() {
        runCatching {
            context.unbindService(this)
            service = null
        }.onFailure {
            AppLog.e("", throwable = it)
        }
    }
}
