package com.ooftf.app.ca

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Vibrator
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.DistanceUtil
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.ooftf.basic.AppHolder
import com.ooftf.basic.armor.InitLiveData
import com.ooftf.log.JLog


object LocationWarn {
    val isLocating = InitLiveData(false);
    val NOTIFICATION_CODE = 1001
    const val KV_KEY_TRACK_CACHE = "KV_KEY_CACHE"
    val locationClient = LocationClient(AppHolder.app).apply {
        //注册监听函数
        registerLocationListener(object : BDAbstractLocationListener() {
            @SuppressLint("MissingPermission")
            override fun onReceiveLocation(bdLocation: BDLocation) {
                if (bdLocation.locType == 62 || bdLocation.locType == 167 || bdLocation.locType == 162) { //文档  http://lbs.baidu.com/index.php?title=android-locsdk/guide/addition-func/error-code
                    //debugProvider?.log("定位错误::${bdLocation.locType}")
                    return
                }
                // JLog.e("onReceiveLocation", "精度::${bdLocation.radius}")
                if (bdLocation.latitude == 0.0 && bdLocation.longitude == 0.0) {
                    return
                }
                if (bdLocation.radius > 300) {
                    JLog.e("百度定位被过滤：精度太低::${bdLocation.radius}")
                    return
                }


                JLog.e("收到百度定位: ${bdLocation.latitude}::${bdLocation.longitude}::${bdLocation.radius}::${bdLocation.locType}")
                JLog.e("UploadTrack", "onReceiveLocation     ok")
                val distance = DistanceUtil.getDistance(
                    LatLng(lat, lng),
                    LatLng(bdLocation.latitude, bdLocation.longitude)
                )
                JLog.e("distance", "定位偏离距离：${distance}，报警距离${area}")
                if (distance > area) {
                    val vibrator =
                        AppHolder.app.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    JLog.e("vibrator.hasVibrator()::${vibrator.hasVibrator()}")
                    if (vibrator.hasVibrator()) {
                        val patter = longArrayOf(1000, 1000)
                        vibrator.vibrate(patter, 0)
                    }
                }
            }
        })
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        //声明LocationClient类实例并配置定位参数
        val locationOption = LocationClientOption()
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll")
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000 * 60)
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true)
        //可选，设置是否需要地址描述 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true)
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false)
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.isLocationNotify = false
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        //locationOption.setIgnoreKillProcess(true)
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(false)
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false)
        //可选，默认false，设置是否开启Gps定位
        locationOption.isOpenGps = true
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false)
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        //locationOption.setOpenAutoNotifyMode()
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        // locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //locationOption.setOpenAutoNotifyMode(10000, 40, LocationClientOption.LOC_SENSITIVITY_HIGHT)
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationOption.isLocationNotify = true
        locationOption.setWifiCacheTimeOut(1000 * 60)
        locOption = locationOption
    }


    fun newNotification(): Notification {
        //开启前台定位服务：
        val builder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationUtils(AppHolder.app).androidChannelNotification
            } else {
                //获取一个Notification构造器
                Notification.Builder(AppHolder.app)
            }
        builder.apply {
            val nfIntent = Intent(
                AppHolder.app,
                MainActivity::class.java
            )
            setContentIntent(
                PendingIntent.getActivity(
                    AppHolder.app,
                    0,
                    nfIntent,
                    0
                )
            ) // 设置PendingIntent
        }
        builder.setContentTitle("正大助手后台定位功能") // 设置下拉列表里的标题
            .setSmallIcon(
                R.mipmap.ic_launcher
            ) // 设置状态栏内的小图标
            .setContentText("正在后台定位") // 设置上下文内容
            .setWhen(System.currentTimeMillis()) // 设置该通知发生的时间
            .setAutoCancel(true)
        val notification = builder.build()
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        return notification
    }

    fun stopLocation() {
        locationClient.disableLocInForeground(true);
        locationClient.stop()
        isLocating.value = false
        (AppHolder.app.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).cancel()
    }

    var lat: Double = 0.0
    var lng: Double = 0.0
    var area: Double = 0.0
    fun setWarnParam(lat: Double, lng: Double, area: Double) {
        LocationWarn.lat = lat
        LocationWarn.lng = lng
        LocationWarn.area = area
    }

    fun startLocation() {
        if (locationClient.isStarted) {
            return
        }
        JLog.e("尝试开启定位")
        //开始定位
        locationClient.enableLocInForeground(NOTIFICATION_CODE, newNotification())
        locationClient.start()
        JLog.e("UploadTrack", "startLocation")
        isLocating.value = true
    }

    fun restartLocation() {
        if (locationClient.isStarted && isLocating.value) {
            locationClient.restart()
        }
    }


    init {
        //网络监听
        registerAppStatusChanged()
    }


    fun registerAppStatusChanged() {
        AppUtils.registerAppStatusChangedListener(object : Utils.OnAppStatusChangedListener {
            override fun onBackground(activity: Activity?) {
                JLog.e("App进入后台")
                JLog.e("UploadTrack", "onBackground")
            }

            override fun onForeground(activity: Activity?) {
                JLog.e("App进入前台")
                JLog.e("UploadTrack", "onForeground")
                restartLocation()
            }
        })
    }
}