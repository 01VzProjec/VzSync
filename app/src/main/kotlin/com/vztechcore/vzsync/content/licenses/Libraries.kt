package com.vztechcore.vzsync.content.licenses

import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.licenses.LicenseType
import com.vztechcore.vzsync.domain.licenses.OpenSourceLibrary

object Libraries {

    val all: List<OpenSourceLibrary> = listOf(
        OpenSourceLibrary(
            name = "Kotlin",
            descriptionRes = R.string.library_kotlin_desc,
            usedForRes = R.string.library_kotlin_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://kotlinlang.org",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "Kotlin Coroutines",
            descriptionRes = R.string.library_coroutines_desc,
            usedForRes = R.string.library_coroutines_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://github.com/Kotlin/kotlinx.coroutines",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "Jetpack Compose",
            descriptionRes = R.string.library_compose_desc,
            usedForRes = R.string.library_compose_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://developer.android.com/jetpack/compose",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "Material 3",
            descriptionRes = R.string.library_material_desc,
            usedForRes = R.string.library_material_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://m3.material.io",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "Navigation Compose",
            descriptionRes = R.string.library_navigation_desc,
            usedForRes = R.string.library_navigation_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://developer.android.com/jetpack/androidx/releases/navigation",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "Lifecycle",
            descriptionRes = R.string.library_lifecycle_desc,
            usedForRes = R.string.library_lifecycle_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://developer.android.com/jetpack/androidx/releases/lifecycle",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "Activity Compose",
            descriptionRes = R.string.library_activity_desc,
            usedForRes = R.string.library_activity_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://developer.android.com/jetpack/androidx/releases/activity",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "Coil 3",
            descriptionRes = R.string.library_coil_desc,
            usedForRes = R.string.library_coil_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://coil-kt.github.io/coil/",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "Retrofit",
            descriptionRes = R.string.library_retrofit_desc,
            usedForRes = R.string.library_retrofit_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://square.github.io/retrofit/",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "OkHttp",
            descriptionRes = R.string.library_okhttp_desc,
            usedForRes = R.string.library_okhttp_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://square.github.io/okhttp/",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "kotlinx.serialization",
            descriptionRes = R.string.library_serialization_desc,
            usedForRes = R.string.library_serialization_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://github.com/Kotlin/kotlinx.serialization",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "JGit",
            descriptionRes = R.string.library_jgit_desc,
            usedForRes = R.string.library_jgit_used,
            license = LicenseType.EDL,
            projectUrl = "https://www.eclipse.org/jgit/",
            licenseUrl = "https://www.eclipse.org/org/documents/edl-v10.php",
        ),
        OpenSourceLibrary(
            name = "AndroidX Security Crypto",
            descriptionRes = R.string.library_security_desc,
            usedForRes = R.string.library_security_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://developer.android.com/jetpack/androidx/releases/security",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "DocumentFile",
            descriptionRes = R.string.library_documentfile_desc,
            usedForRes = R.string.library_documentfile_used,
            license = LicenseType.APACHE_2,
            projectUrl = "https://developer.android.com/jetpack/androidx/releases/documentfile",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        ),
        OpenSourceLibrary(
            name = "Plus Jakarta Sans",
            descriptionRes = R.string.library_jakarta_desc,
            usedForRes = R.string.library_jakarta_used,
            license = LicenseType.OFL,
            projectUrl = "https://fonts.google.com/specimen/Plus+Jakarta+Sans",
            licenseUrl = "https://scripts.sil.org/OFL",
        ),
    )
}