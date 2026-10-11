package com.hoang.common.presentation.initiate

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext

@Composable
fun PermissionChecker(
    permissions: List<String> = listOf(),
    intents: List<String> = listOf(),
    onAllLaunched: () -> Unit,
) {
    val context = LocalContext.current

    val permissionStates =
        remember(permissions) {
            permissions.map { context.hasPermission(it) }.toMutableStateList()
        }
    val intentStates =
        remember(intents) {
            intents.map { context.hasIntentPermission(it) }.toMutableStateList()
        }

    val permissionLaunchers =
        permissions.indices.map { i ->
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) permissionStates[i] = true
            }
        }

    val intentLaunchers =
        intents.indices.map { i ->
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (context.hasIntentPermission(intents[i])) {
                    intentStates[i] = true
                }
            }
        }

    val pendingCount = permissionStates.count { !it } + intentStates.count { !it }

    LaunchedEffect(pendingCount) {
        if (pendingCount == 0) {
            onAllLaunched()
        } else {
            launchNextPending(
                intentStates,
                intentLaunchers,
                intents,
                permissionStates,
                permissionLaunchers,
                permissions,
            )
        }
    }
}

// region Extension Functions
private fun Context.hasPermission(permission: String): Boolean =
    packageManager.checkPermission(permission, packageName) == PERMISSION_GRANTED

private fun Context.hasIntentPermission(intentAction: String): Boolean =
    when (intentAction) {
        Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION ->
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()
        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
            } else {
                true
            }
        else -> false
    }

private fun launchNextPending(
    intentStates: List<Boolean>,
    intentLaunchers: List<ManagedActivityResultLauncher<Intent, ActivityResult>>,
    intents: List<String>,
    permissionStates: List<Boolean>,
    permissionLaunchers: List<ManagedActivityResultLauncher<String, Boolean>>,
    permissions: List<String>,
) {
    val intentIndex = intentStates.indexOfFirst { !it }
    if (intentIndex >= 0) {
        intentLaunchers[intentIndex].launch(Intent(intents[intentIndex]))
        return
    }

    val permissionIndex = permissionStates.indexOfFirst { !it }
    if (permissionIndex >= 0) {
        permissionLaunchers[permissionIndex].launch(permissions[permissionIndex])
    }
}

// endregion
