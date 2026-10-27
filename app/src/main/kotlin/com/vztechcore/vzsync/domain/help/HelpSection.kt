package com.vztechcore.vzsync.domain.help

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.ui.graphics.vector.ImageVector
import com.vztechcore.vzsync.R

enum class HelpSection(
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val icon: ImageVector,
    val keywords: List<String> = emptyList(),
) {
    GETTING_STARTED(
        titleRes = R.string.help_gs_title,
        subtitleRes = R.string.help_gs_subtitle,
        icon = Icons.Rounded.PlayArrow,
        keywords = listOf(
            "primeiros", "passos", "comece", "aqui", "início", "inicio",
            "tutorial", "começar", "comecar", "start", "novo", "new",
            "getting started", "getting", "started", "begin", "home", "guide",
        ),
    ),
    GITHUB_TOKEN(
        titleRes = R.string.help_gt_title,
        subtitleRes = R.string.help_gt_subtitle,
        icon = Icons.Rounded.Key,
        keywords = listOf(
            "conectar", "conectar o github", "github", "token", "pat",
            "personal access token", "conta", "login", "account",
            "credentials", "auth", "authenticate", "connect", "conexão",
            "conexao", "escopo", "escopos", "scope", "scopes", "segurança",
            "seguranca", "security",
        ),
    ),
    FOLDER_SELECTION(
        titleRes = R.string.help_fs_title,
        subtitleRes = R.string.help_fs_subtitle,
        icon = Icons.Rounded.FolderOpen,
        keywords = listOf(
            "escolher", "pasta", "pastas", "folder", "choose", "saf", "storage",
            "arquivo", "arquivos", "caminho", "path", "file", "files",
            "access", "permission", "permissão", "permissao", "armazenamento",
        ),
    ),
    HOME_DRAWER(
        titleRes = R.string.help_hd_title,
        subtitleRes = R.string.help_hd_subtitle,
        icon = Icons.Rounded.Folder,
        keywords = listOf(
            "menu", "lateral", "drawer", "árvore", "arvore", "estrutura",
            "arquivos", "pastas", "tree", "file tree", "structure",
            "side menu", "hamburger", "árvore de arquivos",
        ),
    ),
    PUSH_OVERVIEW(
        titleRes = R.string.help_po_title,
        subtitleRes = R.string.help_po_subtitle,
        icon = Icons.Rounded.CloudUpload,
        keywords = listOf(
            "como", "funciona", "funcionar", "envio", "enviar", "push",
            "fluxo", "flow", "git", "commit", "sending", "upload",
            "publish", "works", "how", "send",
        ),
    ),
    CREATE_REPO(
        titleRes = R.string.help_cr_title,
        subtitleRes = R.string.help_cr_subtitle,
        icon = Icons.Rounded.Add,
        keywords = listOf(
            "criar", "create", "novo", "new", "nome", "name", "público",
            "publico", "public", "privado", "private", "visibilidade",
            "visibility", "repositório", "repositorio", "repository",
        ),
    ),
    EXISTING_REPO(
        titleRes = R.string.help_er_title,
        subtitleRes = R.string.help_er_subtitle,
        icon = Icons.Rounded.Public,
        keywords = listOf(
            "existente", "existing", "atualizar", "update", "sincronizar",
            "sync", "reconstruir", "rebuild", "reconstruct", "overwrite",
            "repositório", "repositorio", "repository",
        ),
    ),
    CLONE_REPO(
        titleRes = R.string.help_cl_title,
        subtitleRes = R.string.help_cl_subtitle,
        icon = Icons.Rounded.Download,
        keywords = listOf(
            "clonar", "clone", "baixar", "download", "url", "github",
            "importar", "import", "fetch", "repositório", "repositorio",
            "repository",
        ),
    ),
    GIT_FOLDER(
        titleRes = R.string.help_gf_title,
        subtitleRes = R.string.help_gf_subtitle,
        icon = Icons.Rounded.Code,
        keywords = listOf(
            "git", ".git", "init", "pasta", "folder", "repositório local",
            "repositorio local", "local repository", "remote", "por que",
            "why", "existe", "exists",
        ),
    ),
    PUSH_SETTINGS(
        titleRes = R.string.help_ps_title,
        subtitleRes = R.string.help_ps_subtitle,
        icon = Icons.Rounded.Settings,
        keywords = listOf(
            "sistema", "system", "push", "configuração", "configuracao",
            "settings", "opção", "opcao", "option", "modo", "mode",
            "ação", "acao", "action", "preference", "explicada", "explained",
        ),
    ),
    LOG_READING(
        titleRes = R.string.help_lr_title,
        subtitleRes = R.string.help_lr_subtitle,
        icon = Icons.Rounded.Terminal,
        keywords = listOf(
            "como", "ler", "read", "log", "terminal", "símbolo", "simbolo",
            "symbol", "comando", "command", "sucesso", "success", "erro",
            "error", "explicado",
        ),
    ),
    THEME(
        titleRes = R.string.help_th_title,
        subtitleRes = R.string.help_th_subtitle,
        icon = Icons.Rounded.Palette,
        keywords = listOf(
            "tema", "theme", "cor", "color", "amoled", "escuro", "dark",
            "claro", "light", "aparência", "aparencia", "appearance",
            "system", "sistema",
        ),
    ),
    ADVANCED_ACTIONS(
        titleRes = R.string.help_aa_title,
        subtitleRes = R.string.help_aa_subtitle,
        icon = Icons.Rounded.Tune,
        keywords = listOf(
            "ações", "acoes", "actions", "avançado", "avancado", "advanced",
            "histórico", "historico", "history", "commit", "desfazer", "undo",
            "descartar", "discard", "branch", "reset", "reverter", "revert",
            "switch", "trocar",
        ),
    ),
    REPOS_SCREEN(
        titleRes = R.string.help_rs_title,
        subtitleRes = R.string.help_rs_subtitle,
        icon = Icons.Rounded.Folder,
        keywords = listOf(
            "meus", "my", "repositórios", "repositorios", "repositories",
            "repositório", "repo", "lista", "list", "detalhes", "details",
            "excluir", "excluir repositório", "apagar", "delete", "remove",
        ),
    ),
    ABOUT_SYSTEM(
        titleRes = R.string.help_ab_title,
        subtitleRes = R.string.help_ab_subtitle,
        icon = Icons.Rounded.Info,
        keywords = listOf(
            "por dentro", "inside", "app", "tecnologia", "technology",
            "jgit", "compose", "sistema", "system", "funciona", "works",
            "biblioteca", "library", "filosofia", "philosophy", "dentro",
        ),
    ),
    FAQ(
        titleRes = R.string.help_faq_title,
        subtitleRes = R.string.help_faq_subtitle,
        icon = Icons.AutoMirrored.Rounded.HelpOutline,
        keywords = listOf(
            "faq", "perguntas", "pergunta", "frequentes", "frequent",
            "frequently asked", "dúvida", "duvida", "doubt", "problema",
            "problem", "erro", "error", "issue", "comum", "common",
        ),
    ),
    GLOSSARY(
        titleRes = R.string.help_gl_title,
        subtitleRes = R.string.help_gl_subtitle,
        icon = Icons.Rounded.Book,
        keywords = listOf(
            "glossário", "glossario", "glossary", "termo", "term",
            "termos", "terms", "definição", "definicao", "definition",
            "significado", "meaning",
        ),
    ),
}