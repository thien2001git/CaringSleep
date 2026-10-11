package com.hoang.common.presentation.service

import android.os.Binder

abstract class BaseServiceBinder<T : BaseService>(
    val service: T,
) : Binder()
