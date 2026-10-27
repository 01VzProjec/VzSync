package com.vztechcore.vzsync.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vztechcore.vzsync.core.util.Formatters
import com.vztechcore.vzsync.domain.LogEntry
import com.vztechcore.vzsync.domain.LogLevel
import com.vztechcore.vzsync.domain.push.LogStyle
import com.vztechcore.vzsync.ui.theme.DarkError
import com.vztechcore.vzsync.ui.theme.DarkSecondary

val TerminalBackground = Color(0xFF000000)

private val CommandGreen = Color(0xFF4ADE80)
private val SuccessColor = Color(0xFF4ADE80)
private val ErrorColor = DarkError
private val TerminalText = Color(0xFFE5E7EB)
private val TerminalDim = Color(0xFF94A3B8)

@Composable
fun LogLineItem(
    entry: LogEntry,
    logStyle: LogStyle = LogStyle.TERMINAL,
    folderPath: String = "",
    lineNumber: Int = 1,
    numberWidth: Int = 1,
    modifier: Modifier = Modifier,
) {
    when (logStyle) {
        LogStyle.TERMINAL -> TerminalLine(
            entry = entry,
            folderPath = folderPath,
            lineNumber = lineNumber,
            numberWidth = numberWidth,
            modifier = modifier,
        )
        LogStyle.LOG -> SimpleLine(
            entry = entry,
            lineNumber = lineNumber,
            numberWidth = numberWidth,
            modifier = modifier,
        )
    }
}

@Composable
private fun TerminalLine(
    entry: LogEntry,
    folderPath: String,
    lineNumber: Int,
    numberWidth: Int,
    modifier: Modifier,
) {
    val numberText = lineNumber.toString().padStart(numberWidth, ' ')

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = "$numberText ",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = TerminalDim,
            softWrap = false,
            maxLines = 1,
        )

        when (entry.level) {
            LogLevel.COMMENT -> {
                Text(
                    text = "# ",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TerminalDim,
                    softWrap = false,
                    maxLines = 1,
                )
                Text(
                    text = entry.message,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = TerminalDim,
                    softWrap = false,
                    maxLines = 1,
                )
            }
            LogLevel.COMMAND -> {
                val short = Formatters.shortFolderPath(folderPath)
                if (short.isNotBlank()) {
                    Text(
                        text = "$short ",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = CommandGreen,
                        softWrap = false,
                        maxLines = 1,
                    )
                }
                Text(
                    text = "$ ",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CommandGreen,
                    softWrap = false,
                    maxLines = 1,
                )
                Text(
                    text = entry.message,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = TerminalText,
                    softWrap = false,
                    maxLines = 1,
                )
            }
            LogLevel.SUCCESS -> {
                Text(
                    text = entry.message,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = TerminalText,
                    softWrap = false,
                    maxLines = 1,
                )
            }
            LogLevel.ERROR -> {
                Text(
                    text = entry.message,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = ErrorColor,
                    softWrap = false,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun SimpleLine(
    entry: LogEntry,
    lineNumber: Int,
    numberWidth: Int,
    modifier: Modifier,
) {
    val numberText = lineNumber.toString().padStart(numberWidth, ' ')

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = "$numberText > ",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = TerminalDim,
            softWrap = false,
            maxLines = 1,
        )

        when (entry.level) {
            LogLevel.COMMENT -> {
                Text(
                    text = entry.message,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = TerminalDim,
                    softWrap = false,
                    maxLines = 1,
                )
            }
            LogLevel.COMMAND -> {
                Text(
                    text = "$: ",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CommandGreen,
                    softWrap = false,
                    maxLines = 1,
                )
                Text(
                    text = entry.message,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = TerminalText,
                    softWrap = false,
                    maxLines = 1,
                )
            }
            LogLevel.SUCCESS -> {
                Text(
                    text = "w: ",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessColor,
                    softWrap = false,
                    maxLines = 1,
                )
                Text(
                    text = entry.message,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = SuccessColor,
                    softWrap = false,
                    maxLines = 1,
                )
            }
            LogLevel.ERROR -> {
                Text(
                    text = "e: ",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ErrorColor,
                    softWrap = false,
                    maxLines = 1,
                )
                Text(
                    text = entry.message,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = ErrorColor,
                    softWrap = false,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun LogBlock(
    logs: List<LogEntry>,
    logStyle: LogStyle,
    folderPath: String,
    modifier: Modifier = Modifier,
    autoScroll: Boolean = true,
) {
    val vScroll = rememberScrollState()
    val hScroll = rememberScrollState()

    LaunchedEffect(logs.size) {
        if (autoScroll && logs.isNotEmpty()) {
            vScroll.animateScrollTo(vScroll.maxValue)
        }
    }

    val selectionColors = TextSelectionColors(
        handleColor = DarkSecondary,
        backgroundColor = DarkSecondary.copy(alpha = 0.3f),
    )

    val numberWidth = logs.size.toString().length.coerceAtLeast(1)

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        SelectionContainer {
            Row(
                modifier = modifier.horizontalScroll(hScroll),
            ) {
                Column(
                    modifier = Modifier.verticalScroll(vScroll),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    logs.forEachIndexed { index, entry ->
                        LogLineItem(
                            entry = entry,
                            logStyle = logStyle,
                            folderPath = folderPath,
                            lineNumber = index + 1,
                            numberWidth = numberWidth,
                        )
                    }
                }
            }
        }
    }
}