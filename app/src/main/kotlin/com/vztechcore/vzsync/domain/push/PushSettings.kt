package com.vztechcore.vzsync.domain.push

enum class DefaultPushMode(val label: String) {
    SYNC("Sincronizar"),
    REBUILD("Reconstruir"),
}

enum class PostPushAction(val label: String, val shortLabel: String) {
    COMMIT_ONLY("Apenas commit", "commit"),
    COMMIT_AND_PUSH("Commit + push", "push"),
}

enum class LogStyle(val label: String) {
    TERMINAL("Terminal"),
    LOG("Log"),
}

data class PushSettings(
    val defaultMode: DefaultPushMode = DefaultPushMode.SYNC,
    val postPushAction: PostPushAction = PostPushAction.COMMIT_AND_PUSH,
    val commitMessageTemplate: String = "Sync via VzSync · {date} {time}",
    val logStyle: LogStyle = LogStyle.TERMINAL,
    val autoShowLog: Boolean = true,
) {
    companion object {
        const val DEFAULT_MESSAGE = "Sync via VzSync · {date} {time}"
    }
}