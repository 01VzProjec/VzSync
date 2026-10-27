package com.vztechcore.vzsync.domain.advanced

enum class AdvancedAction(
    val title: String,
    val shortDescription: String,
    val isDestructive: Boolean,
) {
    VIEW_LAST_COMMIT(
        title = "Ver último commit",
        shortDescription = "Mostra os detalhes do commit mais recente.",
        isDestructive = false,
    ),
    UNDO_LAST_COMMIT(
        title = "Desfazer último commit",
        shortDescription = "Remove o último commit local. Os arquivos permanecem.",
        isDestructive = true,
    ),
    DISCARD_LOCAL_CHANGES(
        title = "Descartar mudanças locais",
        shortDescription = "Descarta alterações não commitadas. Não tem volta.",
        isDestructive = true,
    ),
    SWITCH_BRANCH(
        title = "Trocar de branch",
        shortDescription = "Altera o branch ativo do repositório local.",
        isDestructive = false,
    ),
}