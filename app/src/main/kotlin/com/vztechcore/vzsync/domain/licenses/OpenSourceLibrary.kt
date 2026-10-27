package com.vztechcore.vzsync.domain.licenses

import androidx.annotation.StringRes

data class OpenSourceLibrary(
    val name: String,
    @StringRes val descriptionRes: Int,
    @StringRes val usedForRes: Int,
    val license: LicenseType,
    val projectUrl: String,
    val licenseUrl: String,
)