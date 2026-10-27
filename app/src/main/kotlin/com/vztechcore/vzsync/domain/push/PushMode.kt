package com.vztechcore.vzsync.domain.push 

enum class PushMode(
    val label: String,
    val description: String,
) {
    SYNC(
        label = "Sincronizar",
        description = "Mantém os arquivos existentes no repositório. Apenas arquivos novos " +
            "ou modificados serão enviados. É um push normal.",
    ),
    REBUILD(
        label = "Reconstruir",
        description = "Apaga tudo que existe no repositório e recria a partir da pasta. " +
            "O conteúdo atual do repositório será perdido.",
    ),
}