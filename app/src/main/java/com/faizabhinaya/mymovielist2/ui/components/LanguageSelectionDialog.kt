package com.faizabhinaya.mymovielist2.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.faizabhinaya.mymovielist2.R

@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.select_language)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.indonesian),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected("in") }
                        .padding(vertical = 12.dp)
                )
                Text(
                    text = stringResource(R.string.english),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected("en") }
                        .padding(vertical = 12.dp)
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.change_language))
            }
        }
    )
}
