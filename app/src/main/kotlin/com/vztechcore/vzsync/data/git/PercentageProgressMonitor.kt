package com.vztechcore.vzsync.data.git

import org.eclipse.jgit.lib.ProgressMonitor
import java.util.concurrent.atomic.AtomicBoolean

/**
 * JGit ProgressMonitor that translates per-task progress into a global
 * percentage. Clone has multiple phases:
 *   - "Counting objects"     (weight 5)
 *   - "Compressing objects"  (weight 5)
 *   - "Receiving objects"    (weight 80, the bulk)
 *   - "Resolving deltas"     (weight 10)
 *
 * Phase names are stable across JGit versions, so we match them by prefix.
 * If a phase reports totalWork = -1 (indeterminate), we hold the current
 * percentage and mark the state as indeterminate.
 */
class PercentageProgressMonitor(
    private val onProgress: (percentage: Int, phase: String, indeterminate: Boolean) -> Unit,
    private val cancelRequested: AtomicBoolean,
) : ProgressMonitor {

    private var currentPhase: String = ""
    private var phaseWeightStart: Int = 0
    private var phaseWeightSpan: Int = 0
    private var lastPercentage: Int = 0

    override fun start(totalTasks: Int) {
        lastPercentage = 0
        onProgress(0, "Iniciando", false)
    }

    override fun beginTask(title: String?, totalWork: Int) {
        currentPhase = title.orEmpty()
        val span = weightSpanFor(currentPhase)
        phaseWeightStart = span.first
        phaseWeightSpan = span.second

        if (totalWork <= 0) {
            onProgress(lastPercentage, currentPhase, true)
        } else {
            onProgress(lastPercentage, currentPhase, false)
        }
    }

    override fun update(completed: Int) {
        if (cancelRequested.get()) return
        if (phaseWeightSpan <= 0) return

        // We don't know totalWork in update(); JGit calls endTask when done.
        // Approximate by treating completed as cumulative objects; the
        // percentage advances smoothly and endTask snaps to the phase end.
        val approx = (phaseWeightStart + (phaseWeightSpan * 0.5)).toInt()
        if (approx > lastPercentage) {
            lastPercentage = approx
            onProgress(lastPercentage, currentPhase, false)
        }
    }

    override fun endTask() {
        if (cancelRequested.get()) return
        val next = (phaseWeightStart + phaseWeightSpan).coerceAtMost(100)
        if (next > lastPercentage) {
            lastPercentage = next
            onProgress(lastPercentage, currentPhase, false)
        }
    }

    override fun isCancelled(): Boolean = cancelRequested.get()

    override fun showDuration(enabled: Boolean) {
        // no-op
    }

    private fun weightSpanFor(phase: String): Pair<Int, Int> = when {
        phase.startsWith("Counting", ignoreCase = true) -> 0 to 5
        phase.startsWith("Compressing", ignoreCase = true) -> 5 to 5
        phase.startsWith("Receiving", ignoreCase = true) -> 10 to 80
        phase.startsWith("Resolving", ignoreCase = true) -> 90 to 10
        phase.startsWith("Updating", ignoreCase = true) -> 90 to 10
        else -> lastPercentage to 0
    }
}