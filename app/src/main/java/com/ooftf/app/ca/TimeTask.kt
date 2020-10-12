package com.ooftf.app.ca

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ooftf.basic.AppHolder
import com.ooftf.log.JLog
import java.util.*

object TimeTask {
    val REQEUST_CODE_START_ALARM = 12
    val REQEUST_CODE_START_END = 11
    var startH = 17
    var startM = 50
    var endH = 23
    var endM = 0
    fun setParams(startH: Int, startM: Int, endH: Int, endM: Int) {
        TimeTask.startH = startH
        TimeTask.startM = startM
        TimeTask.endH = endH
        TimeTask.endM = endM
    }

    fun startClock() {
        val current = Calendar.getInstance()
        val start = getStartTime()
        val end = getEndTime()
        setAlarm(
            AlarmManager.RTC_WAKEUP,
            start.get(Calendar.HOUR_OF_DAY),
            start.get(Calendar.MINUTE),
            AlarmManager.INTERVAL_DAY,
            REQEUST_CODE_START_ALARM,
            TimingReceiver.ACTION_ALARM_LOCATION_START
        )
        setAlarm(
            AlarmManager.RTC_WAKEUP,
            end.get(Calendar.HOUR_OF_DAY),
            end.get(Calendar.MINUTE),
            AlarmManager.INTERVAL_DAY,
            REQEUST_CODE_START_END,
            TimingReceiver.ACTION_ALARM_LOCATION_END
        )
        JLog.e("" + current.after(start) + "," + current.before(end))
        if (current.after(start) && current.before(end)) {
            LocationWarn.startLocation()
        }

    }

    fun getStartTime(): Calendar {
        return Calendar.getInstance().apply {
            set(
                Calendar.HOUR_OF_DAY,
                startH
            )
            set(Calendar.MINUTE, startM)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    fun getEndTime(): Calendar {
        return Calendar.getInstance().apply {
            set(
                Calendar.HOUR_OF_DAY,
                endH
            )
            set(Calendar.MINUTE, endM)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    fun setAlarm(
        type: Int,
        triggerAtMillis: Long,
        intervalMillis: Long,
        requestCode: Int,
        action: String
    ) {
        val alarmManager: AlarmManager =
            AppHolder.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            type, triggerAtMillis, intervalMillis, PendingIntent.getBroadcast(
                AppHolder.app,
                requestCode,
                Intent(AppHolder.app, TimingReceiver::class.java).setAction(action),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    fun cancelClock() {
        cancel(REQEUST_CODE_START_ALARM, TimingReceiver.ACTION_ALARM_LOCATION_START)
        cancel(REQEUST_CODE_START_END, TimingReceiver.ACTION_ALARM_LOCATION_END)
    }

    private fun cancel(requestCode: Int, action: String) {
        val alarmManager: AlarmManager =
            AppHolder.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                AppHolder.app,
                requestCode,
                Intent(AppHolder.app, TimingReceiver::class.java).setAction(action),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }


    fun setAlarm(
        type: Int,
        hour: Int,
        minute: Int,
        intervalMillis: Long,
        requestCode: Int,
        action: String
    ) {
        val now = Calendar.getInstance()
        val targetTime = now.clone() as Calendar
        targetTime.set(Calendar.HOUR_OF_DAY, hour)
        targetTime.set(Calendar.MINUTE, minute)
        targetTime.set(Calendar.SECOND, 0)
        targetTime.set(Calendar.MILLISECOND, 0)
        if (targetTime.before(now))
            targetTime.add(Calendar.DATE, 1)
        setAlarm(
            type,
            targetTime.timeInMillis,
            intervalMillis,
            requestCode,
            action
        )
    }
}