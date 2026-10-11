package com.hoang.common.domain.exception

import androidx.annotation.StringRes

sealed class AppException(
    override val message: String? = null,
    @StringRes open val messageResId: Int? = null,
    override val cause: Throwable? = null,
) : Exception(message, cause)
