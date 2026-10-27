package com.vztechcore.vzsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.domain.TreeItem
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurface
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileInfoSheet(
    file: TreeItem,
    onOpenInOtherApp: () -> Unit,
    onClose: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = VzSurface,
        contentColor = VzOnSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Sobre este arquivo",
                style = MaterialTheme.typography.titleLarge,
                color = VzOnSurface,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = file.name,
                style = MaterialTheme.typography.titleMedium,
                color = DarkSecondary,
                fontWeight = FontWeight.SemiBold,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(VzSurfaceVariant)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Por que não consigo ver o conteúdo?",
                    style = MaterialTheme.typography.titleMedium,
                    color = VzOnSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "O VzSync foi feito exclusivamente para enviar e atualizar " +
                        "arquivos em repositórios do GitHub. Ele não abre, edita ou " +
                        "exibe o conteúdo de códigos — esse não é o foco do app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                )
                Text(
                    text = "Se precisar ver o conteúdo, abra o arquivo em outro aplicativo " +
                        "usando o botão abaixo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                )
            }

            Button(
                onClick = onOpenInOtherApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkPrimary,
                    contentColor = VzOnSurface,
                ),
            ) {
                Icon(
                    imageVector = Icons.Rounded.OpenInNew,
                    contentDescription = null,
                )
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(
                    text = "Abrir arquivos em...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Button(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VzSurfaceVariant,
                    contentColor = VzOnSurface,
                ),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                )
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(
                    text = "Fechar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}