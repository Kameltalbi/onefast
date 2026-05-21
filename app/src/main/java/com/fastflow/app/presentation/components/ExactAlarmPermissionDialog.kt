package com.fastflow.app.presentation.components

import android.os.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fastflow.app.R

@Composable
fun ExactAlarmPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.exact_alarm_permission_title)) },
        text = { Text(stringResource(R.string.exact_alarm_permission_body)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.exact_alarm_permission_allow))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.exact_alarm_permission_later))
            }
        }
    )
}
