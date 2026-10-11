package com.hoang.common.utils

import android.util.Log

object AppLog {
    private const val TAG = "MoneyCare"

    fun v(
        message: String,
        throwable: Throwable? = null,
    ) {
        runCatching {
            Log.v(TAG, message.formatMessage(), throwable)
        }.onFailure {
            println("VERBOSE: $TAG: $message ${throwable?.message ?: ""}")
        }
    }

    fun d(
        message: String,
        throwable: Throwable? = null,
    ) {
        runCatching {
            Log.d(TAG, message.formatMessage(), throwable)
        }.onFailure {
            println("DEBUG: $TAG: $message ${throwable?.message ?: ""}")
        }
    }

    fun i(
        message: String,
        throwable: Throwable? = null,
    ) {
        runCatching {
            Log.i(TAG, message.formatMessage(), throwable)
        }.onFailure {
            println("INFO: $TAG: $message ${throwable?.message ?: ""}")
        }
    }

    fun w(
        message: String,
        throwable: Throwable? = null,
    ) {
        runCatching {
            Log.w(TAG, message.formatMessage(), throwable)
        }.onFailure {
            println("WARN: $TAG: $message ${throwable?.message ?: ""}")
        }
    }

    fun e(
        message: String,
        throwable: Throwable? = null,
    ) {
        runCatching {
            Log.e(TAG, message.formatMessage(), throwable)
        }.onFailure {
            println("ERROR: $TAG: $message ${throwable?.message ?: ""}")
        }
    }

    fun wtf(
        message: String,
        throwable: Throwable? = null,
    ) {
        runCatching {
            Log.wtf(TAG, message.formatMessage(), throwable)
        }.onFailure {
            println("WTF: $TAG: $message ${throwable?.message ?: ""}")
        }
    }

    private fun String.formatMessage(): String =
        runCatching {
            Throwable("log").stackTrace.getOrNull(3)?.let {
                "${it.fileName}:${it.lineNumber}:${it.className}:${it.methodName} $this"
            } ?: this
        }.getOrDefault(this)
}
