package com.hoang.common.utils

class GlobalCrashHandler(
    private val defaultHandler: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(
        thread: Thread,
        throwable: Throwable,
    ) {
        // 1. Perform your custom logic (e.g., Log to a file or database)
        AppLog.e("Crash detected in thread: ${thread.name}", throwable)

        // 2. Optional: Navigate to a custom "Error Activity"
        // Note: This can be tricky as the Process is in an unstable state

        // 3. Always hand over to the default OS handler or Firebase Crashlytics
        // so the app actually closes/reports correctly.
        defaultHandler?.uncaughtException(thread, throwable)
    }
}
