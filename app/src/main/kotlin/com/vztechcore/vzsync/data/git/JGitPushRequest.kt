package com.vztechcore.vzsync.data.git

import com.vztechcore.vzsync.domain.push.PushMode
import java.io.File

data class JGitPushRequest(
    val folder: File,
    val repoUrl: String,
    val username: String,
    val token: String,
    val commitMessage: String,
    val mode: PushMode,
    val branch: String = "main",
)