package com.foodchain.iotsolution.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foodchain.iotsolution.ui.theme.IoTSolutionTheme
import com.foodchain.iotsolution.ui.theme.LocalSpacing

@Composable
fun ErrorMessage(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(spacing.medium))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(spacing.large))

            Button(onClick = onRetry) {
                Text(text = "Retry")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorMessagePreview() {
    IoTSolutionTheme {
        ErrorMessage(
            message = "Something went wrong. Please try again.",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorMessageNoRetryPreview() {
    IoTSolutionTheme {
        ErrorMessage(
            message = "Something went wrong."
        )
    }
}
