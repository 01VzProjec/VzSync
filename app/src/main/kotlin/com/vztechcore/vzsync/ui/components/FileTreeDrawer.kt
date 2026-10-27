package com.vztechcore.vzsync.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.TreeItem
import com.vztechcore.vzsync.ui.theme.DarkError
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant

private data class FlatNode(
    val item: TreeItem,
    val depth: Int,
    val isLast: Boolean,
    val ancestorsAreLast: List<Boolean>,
)

private val TreeLineColor = Color(0xFF94A3B8).copy(alpha = 0.55f)
private val TreeCellWidth: Dp = 18.dp
private val TreeRowHeight: Dp = 32.dp
private val TreeLineThickness: Dp = 1.5.dp

@Composable
fun FileTreeDrawerContent(
    tree: TreeItem?,
    isLoading: Boolean,
    error: String?,
    expandedPaths: Set<String>,
    onToggleExpand: (String) -> Unit,
    onFileClick: (TreeItem) -> Unit,
    onRefresh: () -> Unit,
    onHelpRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.drawer_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = VzOnSurface,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.drawer_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                )
            }
            IconButton(onClick = onHelpRequested) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                    contentDescription = stringResource(R.string.drawer_help_action),
                    tint = VzOnSurfaceVariant,
                )
            }
            IconButton(onClick = onRefresh, enabled = !isLoading) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.drawer_refresh_action),
                    tint = if (isLoading) VzOnSurfaceVariant else VzOnSurface,
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                isLoading -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = DarkSecondary)
                }

                error != null -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(DarkError.copy(alpha = 0.12f))
                        .padding(14.dp),
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkError,
                    )
                }

                tree == null -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.drawer_empty),
                        style = MaterialTheme.typography.bodySmall,
                        color = VzOnSurfaceVariant,
                    )
                }

                else -> {
                    val flattened = flattenTree(tree, expandedPaths)
                    val maxDepth = flattened.maxOfOrNull { it.depth } ?: 0
                    val minContentWidth = (TreeCellWidth * (maxDepth + 1)) + 320.dp
                    val hScroll = rememberScrollState()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(hScroll),
                    ) {
                        LazyColumn(
                            modifier = Modifier.width(minContentWidth),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        ) {
                            items(flattened) { node ->
                                TreeRow(
                                    node = node,
                                    isExpanded = node.item.path in expandedPaths,
                                    onClick = {
                                        if (node.item.isDirectory) {
                                            onToggleExpand(node.item.path)
                                        } else {
                                            onFileClick(node.item)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.footer_branding),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurface,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.footer_personal_project),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TreeRow(
    node: FlatNode,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 160),
        label = "chevron",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TreeRowHeight)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TreeLinesCanvas(
            depth = node.depth,
            isLast = node.isLast,
            ancestorsAreLast = node.ancestorsAreLast,
        )

        if (node.item.isDirectory) {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = VzOnSurfaceVariant,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(rotation),
            )
        } else {
            Spacer(Modifier.size(18.dp))
        }

        Spacer(Modifier.width(4.dp))

        Icon(
            imageVector = when {
                !node.item.isDirectory -> Icons.Rounded.InsertDriveFile
                isExpanded -> Icons.Rounded.FolderOpen
                else -> Icons.Rounded.Folder
            },
            contentDescription = null,
            tint = if (node.item.isDirectory) DarkSecondary else VzOnSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = node.item.name,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurface,
            fontWeight = if (node.item.isDirectory) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            softWrap = false,
        )
    }
}

@Composable
private fun TreeLinesCanvas(
    depth: Int,
    isLast: Boolean,
    ancestorsAreLast: List<Boolean>,
) {
    if (depth == 0) {
        Spacer(Modifier.width(0.dp))
        return
    }

    Canvas(
        modifier = Modifier
            .width(TreeCellWidth * depth)
            .fillMaxHeight(),
    ) {
        val cellPx = TreeCellWidth.toPx()
        val thicknessPx = TreeLineThickness.toPx()
        val centerY = size.height / 2f
        val halfCell = cellPx / 2f
        val bleed = thicknessPx / 2f

        for (i in 0 until depth - 1) {
            if (!ancestorsAreLast[i + 1]) {
                val x = i * cellPx + halfCell
                drawLine(
                    color = TreeLineColor,
                    start = Offset(x, -bleed),
                    end = Offset(x, size.height + bleed),
                    strokeWidth = thicknessPx,
                )
            }
        }

        val connX = (depth - 1) * cellPx + halfCell

        if (isLast) {
            drawLine(
                color = TreeLineColor,
                start = Offset(connX, -bleed),
                end = Offset(connX, centerY),
                strokeWidth = thicknessPx,
            )
        } else {
            drawLine(
                color = TreeLineColor,
                start = Offset(connX, -bleed),
                end = Offset(connX, size.height + bleed),
                strokeWidth = thicknessPx,
            )
        }

        drawLine(
            color = TreeLineColor,
            start = Offset(connX, centerY),
            end = Offset(cellPx * depth, centerY),
            strokeWidth = thicknessPx,
        )
    }
}

private fun flattenTree(
    root: TreeItem,
    expanded: Set<String>,
): List<FlatNode> {
    val out = mutableListOf<FlatNode>()

    fun walk(node: TreeItem, depth: Int, isLast: Boolean, ancestorsAreLast: List<Boolean>) {
        out += FlatNode(node, depth, isLast, ancestorsAreLast)
        if (node.isDirectory && node.path in expanded) {
            node.children.forEachIndexed { index, child ->
                walk(
                    node = child,
                    depth = depth + 1,
                    isLast = index == node.children.lastIndex,
                    ancestorsAreLast = ancestorsAreLast + isLast,
                )
            }
        }
    }

    walk(root, 0, true, emptyList())
    return out
}