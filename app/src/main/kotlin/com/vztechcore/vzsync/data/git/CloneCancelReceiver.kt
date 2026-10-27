package com.vztechcore.vzsync.data.git

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CloneCancelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        CloneProgressBus.requestCancel()
    }
}