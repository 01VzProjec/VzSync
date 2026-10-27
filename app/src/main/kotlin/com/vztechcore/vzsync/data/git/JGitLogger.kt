package com.vztechcore.vzsync.data.git

import com.vztechcore.vzsync.domain.LogLevel

fun interface JGitLogger {
    fun log(level: LogLevel, message: String)
}