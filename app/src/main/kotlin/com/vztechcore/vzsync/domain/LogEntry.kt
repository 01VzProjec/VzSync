package com.vztechcore.vzsync.domain

enum class LogLevel {
    COMMENT,   // rendered as "# "
    COMMAND,   // rendered as "$-"
    SUCCESS,   // rendered as "w-"
    ERROR,     // rendered as "e-"
}

data class LogEntry(
    val level: LogLevel,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
)