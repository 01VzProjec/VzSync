package com.vztechcore.vzsync.ui.screens.licenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.domain.licenses.LicenseCategory
import com.vztechcore.vzsync.domain.licenses.OpenSourceLibrary

/**
 * Fixed color per license category.
 * Green  -> permissive
 * Amber  -> has conditions
 * Red    -> restrictive
 */
fun licenseColor(category: LicenseCategory): Color = when (category) {
    LicenseCategory.LIBERAL -> Color(0xFF22C55E)
    LicenseCategory.MODERATE -> Color(0xFFF59E0B)
    LicenseCategory.STRICT -> Color(0xFFEF4444)
}

@Composable
fun LicenseBadge(library: OpenSourceLibrary) {
    val color = licenseColor(library.license.category)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = library.license.label,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
        )
    }
}