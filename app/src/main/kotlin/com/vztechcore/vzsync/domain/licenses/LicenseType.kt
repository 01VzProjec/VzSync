package com.vztechcore.vzsync.domain.licenses

enum class LicenseType(
    val label: String,
    val category: LicenseCategory,
) {
    APACHE_2("Apache 2.0", LicenseCategory.LIBERAL),
    MIT("MIT", LicenseCategory.LIBERAL),
    BSD_3("BSD-3", LicenseCategory.LIBERAL),
    EDL("EDL", LicenseCategory.LIBERAL),
    ISC("ISC", LicenseCategory.LIBERAL),
    OFL("OFL 1.1", LicenseCategory.MODERATE),
    MPL("MPL 2.0", LicenseCategory.MODERATE),
    LGPL("LGPL", LicenseCategory.MODERATE),
    GPL("GPL", LicenseCategory.STRICT),
    AGPL("AGPL", LicenseCategory.STRICT),
}