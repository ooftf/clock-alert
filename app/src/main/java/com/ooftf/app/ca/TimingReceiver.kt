package com.ooftf.app.ca

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ooftf.log.JLog

class TimingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        JLog.e("onReceive")
        val action = intent?.action
        when {
            ACTION_ALARM_LOCATION_START == action -> start(context)
            ACTION_ALARM_LOCATION_END == action -> end(context)
        }
    }

    private fun start(context: Context?) {
        JLog.e("onReceive start")
        LocationWarn.startLocation()
    }

    /**
     * 执行补充库存动作, 即下单/定货
     */
    private fun end(context: Context?) {
        JLog.e("onReceive end")
        LocationWarn.stopLocation()
    }

    companion object {
        val ACTION_ALARM_LOCATION_START = "action.alarm.location.start.action"
        val ACTION_ALARM_LOCATION_END = "action.alarm.location.end.action"
    }
}