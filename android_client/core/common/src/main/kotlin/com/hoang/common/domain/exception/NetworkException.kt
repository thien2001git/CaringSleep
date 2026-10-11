package com.hoang.common.domain.exception

import androidx.annotation.StringRes
import com.hoang.common.R

data class NetworkException(
    override val message: String? = "Network connection error",
    @StringRes override val messageResId: Int = R.string.error_network,
    override val cause: Throwable? = null,
) : AppException(message, messageResId, cause)
