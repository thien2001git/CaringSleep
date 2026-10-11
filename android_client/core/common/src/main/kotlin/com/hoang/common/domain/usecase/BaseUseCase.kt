package com.hoang.common.domain.usecase

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.hoang.common.domain.entity.Resource
import com.hoang.common.domain.exception.AppException
import com.hoang.common.domain.exception.AuthException
import com.hoang.common.domain.exception.UnknownAppException
import com.hoang.common.utils.AppLog
import java.util.concurrent.ExecutionException

abstract class BaseUseCase<Params : BaseUseCase.Params, Result> {
    abstract suspend fun doTask(params: Params): Result

    suspend operator fun invoke(params: Params): Resource<Result> =
        runCatching {
            Resource.Success(doTask(params))
        }.getOrElse { throwable ->
            val appException = throwable.mapToAppException()
            val errorMessage = appException.message ?: "An unknown error occurred"
            AppLog.e("Error $errorMessage with params: $params", appException)

            Resource.Error(errorMessage, appException)
        }

    private fun Throwable.mapToAppException(): AppException {
        val target = if (this is ExecutionException && cause != null) cause!! else this
        return when (target) {
            is AppException -> target
            is FirebaseAuthInvalidCredentialsException -> AuthException.InvalidCredentialsException(cause = target)
            is FirebaseAuthUserCollisionException -> AuthException.EmailAlreadyInUseException(cause = target)
            is FirebaseAuthInvalidUserException -> AuthException.UserNotFoundException(cause = target)
            else -> UnknownAppException(message = target.message ?: "An unknown error occurred", cause = target)
        }
    }

    abstract class Params
}
