package com.hoang.common.presentation.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

abstract class BaseWorker(
    context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {
    sealed class WorkerConfig
}
