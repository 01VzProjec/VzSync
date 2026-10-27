package com.vztechcore.vzsync.data.repos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRepoRequest(
    val name: String,
    val description: String? = null,
    @SerialName("private") val isPrivate: Boolean = false,
    @SerialName("auto_init") val autoInit: Boolean = false,
)