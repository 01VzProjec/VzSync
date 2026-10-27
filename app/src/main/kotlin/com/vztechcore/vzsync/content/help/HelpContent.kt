package com.vztechcore.vzsync.content.help

import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.help.HelpBlock
import com.vztechcore.vzsync.domain.help.HelpSection

object HelpContent {

    fun blocksFor(section: HelpSection): List<HelpBlock> = when (section) {
        HelpSection.GETTING_STARTED -> listOf(
            HelpBlock.Paragraph(R.string.help_gs_01),
            HelpBlock.Heading(R.string.help_gs_02),
            HelpBlock.Bullet(R.string.help_gs_03),
            HelpBlock.Bullet(R.string.help_gs_04),
            HelpBlock.Bullet(R.string.help_gs_05),
            HelpBlock.Bullet(R.string.help_gs_06),
            HelpBlock.Tip(R.string.help_gs_07),
            HelpBlock.Paragraph(R.string.help_gs_08),
        )

        HelpSection.GITHUB_TOKEN -> listOf(
            HelpBlock.Paragraph(R.string.help_gt_01),
            HelpBlock.Heading(R.string.help_gt_02),
            HelpBlock.Paragraph(R.string.help_gt_03),
            HelpBlock.Heading(R.string.help_gt_04),
            HelpBlock.Bullet(R.string.help_gt_05),
            HelpBlock.Bullet(R.string.help_gt_06),
            HelpBlock.Bullet(R.string.help_gt_07),
            HelpBlock.Bullet(R.string.help_gt_08),
            HelpBlock.Heading(R.string.help_gt_09),
            HelpBlock.Paragraph(R.string.help_gt_10),
            HelpBlock.Warning(R.string.help_gt_11),
            HelpBlock.Tip(R.string.help_gt_12),
        )

        HelpSection.FOLDER_SELECTION -> listOf(
            HelpBlock.Paragraph(R.string.help_fs_01),
            HelpBlock.Heading(R.string.help_fs_02),
            HelpBlock.Paragraph(R.string.help_fs_03),
            HelpBlock.Heading(R.string.help_fs_04),
            HelpBlock.Paragraph(R.string.help_fs_05),
            HelpBlock.Tip(R.string.help_fs_06),
        )

        HelpSection.HOME_DRAWER -> listOf(
            HelpBlock.Paragraph(R.string.help_hd_01),
            HelpBlock.Heading(R.string.help_hd_02),
            HelpBlock.Bullet(R.string.help_hd_03),
            HelpBlock.Bullet(R.string.help_hd_04),
            HelpBlock.Bullet(R.string.help_hd_05),
            HelpBlock.Bullet(R.string.help_hd_06),
            HelpBlock.Heading(R.string.help_hd_07),
            HelpBlock.Paragraph(R.string.help_hd_08),
            HelpBlock.Tip(R.string.help_hd_09),
        )

        HelpSection.PUSH_OVERVIEW -> listOf(
            HelpBlock.Paragraph(R.string.help_po_01),
            HelpBlock.Heading(R.string.help_po_02),
            HelpBlock.Bullet(R.string.help_po_03),
            HelpBlock.Bullet(R.string.help_po_04),
            HelpBlock.Bullet(R.string.help_po_05),
            HelpBlock.Bullet(R.string.help_po_06),
            HelpBlock.Bullet(R.string.help_po_07),
            HelpBlock.Tip(R.string.help_po_08),
            HelpBlock.Heading(R.string.help_po_09),
            HelpBlock.Paragraph(R.string.help_po_10),
        )

        HelpSection.CREATE_REPO -> listOf(
            HelpBlock.Paragraph(R.string.help_cr_01),
            HelpBlock.Heading(R.string.help_cr_02),
            HelpBlock.Paragraph(R.string.help_cr_03),
            HelpBlock.Code(R.string.help_cr_04),
            HelpBlock.Heading(R.string.help_cr_05),
            HelpBlock.Bullet(R.string.help_cr_06),
            HelpBlock.Bullet(R.string.help_cr_07),
            HelpBlock.Warning(R.string.help_cr_08),
        )

        HelpSection.EXISTING_REPO -> listOf(
            HelpBlock.Paragraph(R.string.help_er_01),
            HelpBlock.Heading(R.string.help_er_02),
            HelpBlock.Paragraph(R.string.help_er_03),
            HelpBlock.Heading(R.string.help_er_04),
            HelpBlock.Paragraph(R.string.help_er_05),
            HelpBlock.Danger(R.string.help_er_06),
            HelpBlock.Tip(R.string.help_er_07),
        )

        HelpSection.CLONE_REPO -> listOf(
            HelpBlock.Paragraph(R.string.help_cl_01),
            HelpBlock.Heading(R.string.help_cl_02),
            HelpBlock.Bullet(R.string.help_cl_03),
            HelpBlock.Bullet(R.string.help_cl_04),
            HelpBlock.Bullet(R.string.help_cl_05),
            HelpBlock.Heading(R.string.help_cl_06),
            HelpBlock.Paragraph(R.string.help_cl_07),
            HelpBlock.Heading(R.string.help_cl_08),
            HelpBlock.Code(R.string.help_cl_09),
            HelpBlock.Warning(R.string.help_cl_10),
            HelpBlock.Tip(R.string.help_cl_11),
            HelpBlock.Heading(R.string.help_cl_12),
            HelpBlock.Paragraph(R.string.help_cl_13),
        )

        HelpSection.GIT_FOLDER -> listOf(
            HelpBlock.Paragraph(R.string.help_gf_01),
            HelpBlock.Heading(R.string.help_gf_02),
            HelpBlock.Paragraph(R.string.help_gf_03),
            HelpBlock.Code(R.string.help_gf_04),
            HelpBlock.Paragraph(R.string.help_gf_05),
            HelpBlock.Warning(R.string.help_gf_06),
            HelpBlock.Tip(R.string.help_gf_07),
        )

        HelpSection.PUSH_SETTINGS -> listOf(
            HelpBlock.Paragraph(R.string.help_ps_01),
            HelpBlock.Heading(R.string.help_ps_02),
            HelpBlock.Paragraph(R.string.help_ps_03),
            HelpBlock.Bullet(R.string.help_ps_04),
            HelpBlock.Bullet(R.string.help_ps_05),
            HelpBlock.Heading(R.string.help_ps_06),
            HelpBlock.Bullet(R.string.help_ps_07),
            HelpBlock.Bullet(R.string.help_ps_08),
            HelpBlock.Heading(R.string.help_ps_09),
            HelpBlock.Paragraph(R.string.help_ps_10),
            HelpBlock.Bullet(R.string.help_ps_11),
            HelpBlock.Bullet(R.string.help_ps_12),
            HelpBlock.Bullet(R.string.help_ps_13),
            HelpBlock.Bullet(R.string.help_ps_14),
            HelpBlock.Bullet(R.string.help_ps_15),
            HelpBlock.Bullet(R.string.help_ps_16),
            HelpBlock.Bullet(R.string.help_ps_17),
            HelpBlock.Bullet(R.string.help_ps_18),
            HelpBlock.Heading(R.string.help_ps_19),
            HelpBlock.Paragraph(R.string.help_ps_20),
            HelpBlock.Bullet(R.string.help_ps_21),
            HelpBlock.Bullet(R.string.help_ps_22),
            HelpBlock.Tip(R.string.help_ps_23),
        )

        HelpSection.LOG_READING -> listOf(
            HelpBlock.Paragraph(R.string.help_lr_01),
            HelpBlock.Heading(R.string.help_lr_02),
            HelpBlock.Bullet(R.string.help_lr_03),
            HelpBlock.Bullet(R.string.help_lr_04),
            HelpBlock.Bullet(R.string.help_lr_05),
            HelpBlock.Bullet(R.string.help_lr_06),
            HelpBlock.Heading(R.string.help_lr_07),
            HelpBlock.Bullet(R.string.help_lr_08),
            HelpBlock.Bullet(R.string.help_lr_09),
            HelpBlock.Bullet(R.string.help_lr_10),
            HelpBlock.Paragraph(R.string.help_lr_11),
        )

        HelpSection.THEME -> listOf(
            HelpBlock.Paragraph(R.string.help_th_01),
            HelpBlock.Bullet(R.string.help_th_02),
            HelpBlock.Bullet(R.string.help_th_03),
            HelpBlock.Bullet(R.string.help_th_04),
            HelpBlock.Bullet(R.string.help_th_05),
            HelpBlock.Tip(R.string.help_th_06),
        )

        HelpSection.ADVANCED_ACTIONS -> listOf(
            HelpBlock.Paragraph(R.string.help_aa_01),
            HelpBlock.Heading(R.string.help_aa_02),
            HelpBlock.Paragraph(R.string.help_aa_03),
            HelpBlock.Tip(R.string.help_aa_04),
            HelpBlock.Heading(R.string.help_aa_05),
            HelpBlock.Paragraph(R.string.help_aa_06),
            HelpBlock.Heading(R.string.help_aa_07),
            HelpBlock.Paragraph(R.string.help_aa_08),
            HelpBlock.Heading(R.string.help_aa_09),
            HelpBlock.Paragraph(R.string.help_aa_10),
            HelpBlock.Heading(R.string.help_aa_11),
            HelpBlock.Paragraph(R.string.help_aa_12),
            HelpBlock.Warning(R.string.help_aa_13),
            HelpBlock.Heading(R.string.help_aa_14),
            HelpBlock.Paragraph(R.string.help_aa_15),
            HelpBlock.Danger(R.string.help_aa_16),
            HelpBlock.Heading(R.string.help_aa_17),
            HelpBlock.Paragraph(R.string.help_aa_18),
            HelpBlock.Tip(R.string.help_aa_19),
        )

        HelpSection.REPOS_SCREEN -> listOf(
            HelpBlock.Paragraph(R.string.help_rs_01),
            HelpBlock.Heading(R.string.help_rs_02),
            HelpBlock.Bullet(R.string.help_rs_03),
            HelpBlock.Bullet(R.string.help_rs_04),
            HelpBlock.Bullet(R.string.help_rs_05),
            HelpBlock.Heading(R.string.help_rs_06),
            HelpBlock.Paragraph(R.string.help_rs_07),
            HelpBlock.Heading(R.string.help_rs_08),
            HelpBlock.Paragraph(R.string.help_rs_09),
            HelpBlock.Tip(R.string.help_rs_10),
            HelpBlock.Heading(R.string.help_rs_11),
            HelpBlock.Paragraph(R.string.help_rs_12),
            HelpBlock.Heading(R.string.help_rs_13),
            HelpBlock.Paragraph(R.string.help_rs_14),
            HelpBlock.Danger(R.string.help_rs_15),
            HelpBlock.Warning(R.string.help_rs_16),
        )

        HelpSection.ABOUT_SYSTEM -> listOf(
            HelpBlock.Paragraph(R.string.help_ab_01),
            HelpBlock.Heading(R.string.help_ab_02),
            HelpBlock.Paragraph(R.string.help_ab_03),
            HelpBlock.Heading(R.string.help_ab_04),
            HelpBlock.Paragraph(R.string.help_ab_05),
            HelpBlock.Heading(R.string.help_ab_06),
            HelpBlock.Paragraph(R.string.help_ab_07),
            HelpBlock.Heading(R.string.help_ab_08),
            HelpBlock.Paragraph(R.string.help_ab_09),
            HelpBlock.Heading(R.string.help_ab_10),
            HelpBlock.Paragraph(R.string.help_ab_11),
            HelpBlock.Tip(R.string.help_ab_12),
        )

        HelpSection.FAQ -> listOf(
            HelpBlock.Heading(R.string.help_faq_01),
            HelpBlock.Paragraph(R.string.help_faq_02),
            HelpBlock.Heading(R.string.help_faq_03),
            HelpBlock.Paragraph(R.string.help_faq_04),
            HelpBlock.Heading(R.string.help_faq_05),
            HelpBlock.Paragraph(R.string.help_faq_06),
            HelpBlock.Heading(R.string.help_faq_07),
            HelpBlock.Paragraph(R.string.help_faq_08),
            HelpBlock.Heading(R.string.help_faq_09),
            HelpBlock.Paragraph(R.string.help_faq_10),
            HelpBlock.Heading(R.string.help_faq_11),
            HelpBlock.Paragraph(R.string.help_faq_12),
            HelpBlock.Heading(R.string.help_faq_13),
            HelpBlock.Paragraph(R.string.help_faq_14),
            HelpBlock.Heading(R.string.help_faq_15),
            HelpBlock.Paragraph(R.string.help_faq_16),
            HelpBlock.Heading(R.string.help_faq_17),
            HelpBlock.Paragraph(R.string.help_faq_18),
        )

        HelpSection.GLOSSARY -> listOf(
            HelpBlock.Heading(R.string.help_gl_01),
            HelpBlock.Paragraph(R.string.help_gl_02),
            HelpBlock.Heading(R.string.help_gl_03),
            HelpBlock.Paragraph(R.string.help_gl_04),
            HelpBlock.Heading(R.string.help_gl_05),
            HelpBlock.Paragraph(R.string.help_gl_06),
            HelpBlock.Heading(R.string.help_gl_07),
            HelpBlock.Paragraph(R.string.help_gl_08),
            HelpBlock.Heading(R.string.help_gl_09),
            HelpBlock.Paragraph(R.string.help_gl_10),
            HelpBlock.Heading(R.string.help_gl_11),
            HelpBlock.Paragraph(R.string.help_gl_12),
            HelpBlock.Heading(R.string.help_gl_13),
            HelpBlock.Paragraph(R.string.help_gl_14),
            HelpBlock.Heading(R.string.help_gl_15),
            HelpBlock.Paragraph(R.string.help_gl_16),
            HelpBlock.Heading(R.string.help_gl_17),
            HelpBlock.Paragraph(R.string.help_gl_18),
            HelpBlock.Heading(R.string.help_gl_19),
            HelpBlock.Paragraph(R.string.help_gl_20),
        )
    }
}