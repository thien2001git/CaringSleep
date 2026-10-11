package com.hoang.common.domain.exception

import androidx.annotation.StringRes
import com.hoang.common.R

sealed class AuthException(
    message: String? = null,
    @StringRes override val messageResId: Int? = null,
    cause: Throwable? = null,
) : AppException(message, messageResId, cause) {
    data class InvalidCredentialsException(
        override val message: String? = "Invalid email or password",
        @StringRes override val messageResId: Int = R.string.error_invalid_credentials,
        override val cause: Throwable? = null,
    ) : AuthException(message, messageResId, cause)

    data class UserNotFoundException(
        override val message: String? = "User account not found",
        @StringRes override val messageResId: Int = R.string.error_user_not_found,
        override val cause: Throwable? = null,
    ) : AuthException(message, messageResId, cause)

    data class EmailAlreadyInUseException(
        override val message: String? = "The email address is already in use by another account",
        @StringRes override val messageResId: Int = R.string.error_email_already_in_use,
        override val cause: Throwable? = null,
    ) : AuthException(message, messageResId, cause)

    data class InvalidTokenException(
        override val message: String? = "Authentication token is invalid or expired",
        @StringRes override val messageResId: Int = R.string.error_invalid_token,
        override val cause: Throwable? = null,
    ) : AuthException(message, messageResId, cause)

    data class GoogleSignInException(
        override val message: String? = "Google sign in failed",
        @StringRes override val messageResId: Int = R.string.error_google_sign_in_failed,
        override val cause: Throwable? = null,
    ) : AuthException(message, messageResId, cause)

    data class UserNullAfterAuthException(
        override val message: String? = "Authenticated user is null",
        @StringRes override val messageResId: Int = R.string.error_user_null_after_auth,
        override val cause: Throwable? = null,
    ) : AuthException(message, messageResId, cause)

    data class UnauthenticatedException(
        override val message: String? = "No authenticated Firebase user found for Google login",
        @StringRes override val messageResId: Int = R.string.error_unauthenticated,
        override val cause: Throwable? = null,
    ) : AuthException(message, messageResId, cause)
}
