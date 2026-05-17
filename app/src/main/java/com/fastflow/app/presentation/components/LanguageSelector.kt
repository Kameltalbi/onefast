package com.fastflow.app.presentation.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fastflow.app.R
import com.fastflow.app.core.locale.AppLanguages
import com.fastflow.app.presentation.theme.AccentBlue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LanguageSelector(
    selectedTag: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.language_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppLanguages.all.forEach { language ->
                val selected = selectedTag == language.tag
                FilterChip(
                    selected = selected,
                    onClick = { onSelect(language.tag) },
                    label = {
                        Text(
                            text = language.nativeLabel,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue.copy(alpha = 0.35f),
                        selectedLabelColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}
