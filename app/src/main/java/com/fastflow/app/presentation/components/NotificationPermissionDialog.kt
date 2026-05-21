package com.fastflow.app.presentation.components

import android.Manifest
import android.os.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fastflow.app.R

@Composable
fun NotificationPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.notif_permission_title)) },
        text = { Text(stringResource(R.string.notif_permission_body)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.notif_permission_allow))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.notif_permission_later))
            }
        }
    )
}

const val POST_NOTIFICATIONS_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
