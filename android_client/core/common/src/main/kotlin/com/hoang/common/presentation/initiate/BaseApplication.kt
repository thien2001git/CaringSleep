package com.hoang.common.presentation.initiate

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.hoang.common.utils.AppLog
import com.hoang.common.utils.GlobalCrashHandler
import com.jakewharton.processphoenix.ProcessPhoenix
import javax.inject.Inject

open class BaseApplication :
    Application(),
    Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(workerFactory)
                .build()

    override fun onCreate() {
        super.onCreate()
        // Ngăn chặn khởi tạo lại nếu đang ở tiến trình của ProcessPhoenix
        if (ProcessPhoenix.isPhoenixProcess(this)) {
            AppLog.i("This is a phoenix process! Aborting initialization of App[onCreate]")
            return
        }

        initCrashHandler()

        // Gọi hàm này để các Application con thực hiện logic riêng
        onAppCreated()
    }

    private fun initCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // Cài đặt custom crash handler làm mặc định
        Thread.setDefaultUncaughtExceptionHandler(
            GlobalCrashHandler(defaultHandler),
        )
    }

    /**
     * Override hàm này ở các lớp kế thừa (PhoneApplication, WearApplication)
     * để khởi tạo các thư viện đặc thù của từng nền tảng.
     */
    protected open fun onAppCreated(): Unit = throw UnsupportedOperationException("onAppCreated() not implemented")
}
