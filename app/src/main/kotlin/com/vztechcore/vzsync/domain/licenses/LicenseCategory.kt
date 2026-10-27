package com.vztechcore.vzsync.domain.licenses

import androidx.annotation.StringRes
import com.vztechcore.vzsync.R

/**
 * Classification used by the UI to color the license badge.
 * - LIBERAL: you can use it freely, minimal conditions (MIT, Apache, BSD).
 * - MODERATE: allowed, but with specific obligations (OFL, MPL, LGPL).
 * - STRICT: copyleft or restrictive (GPL, AGPL).
 */
enum class LicenseCategory(@StringRes val labelRes: Int) {
    LIBERAL(R.string.libraries_license_liberal),
    MODERATE(R.string.libraries_license_moderate),
    STRICT(R.string.libraries_license_strict),
}