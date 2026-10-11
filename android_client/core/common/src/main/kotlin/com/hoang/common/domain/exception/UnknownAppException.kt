package com.hoang.common.domain.exception

import androidx.annotation.StringRes
import com.hoang.common.R

data class UnknownAppException(
    override val message: String? = "An unknown error occurred",
    @StringRes override val messageResId: Int = R.string.error_unknown,
    override val cause: Throwable? = null,
) : AppException(message, messageResId, cause)
