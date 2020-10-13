package com.ooftf.app.ca

import android.Manifest
import android.app.Application
import androidx.fragment.app.FragmentActivity
import com.ooftf.app.ca.LocationHelper.LocationResult
import com.ooftf.arch.frame.mvvm.vm.BaseViewModel
import com.ooftf.basic.armor.InitLiveData
import com.ooftf.basic.utils.toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

/**
 *
 * @author ooftf
 * @email 994749769@qq.com
 * @date 2020/9/30
 */
class MainViewModel(application: Application) : BaseViewModel(application) {
    val data = CAData()
    val isRunning = InitLiveData(false)
    val startH = InitLiveData("17")
    val startM = InitLiveData("50")
    val endH = InitLiveData("23")
    val endM = InitLiveData("00")

    init {
        data.longitude.value = "116.317372"
        data.latitude.value = "39.982946"
        data.area.value = "60"
    }

    fun getCoordinate() {
        LocationHelper.getLocation()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<LocationResult> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(t: LocationResult) {
                    data.longitude.value = t.longitude.toString()
                    data.latitude.value = t.latitude.toString()
                }

                override fun onError(e: Throwable) {
                    toast("获取经纬度失败")
                }

            })


    }

    fun startService() {
        (getActivity() as? FragmentActivity)?.let {
            permissions(
                it, "需要获取定位权限", Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) {
                TimeTask.setParams(
                    startH.value.toInt(),
                    startM.value.toInt(),
                    endH.value.toInt(),
                    endM.value.toInt()
                )
                TimeTask.startClock()
                LocationWarn.setWarnParam(
                    data.latitude.value.toDouble(),
                    data.longitude.value.toDouble(),
                    data.area.value.toDouble()
                )
                isRunning.value = true
            }
        }

    }

    fun stopVibrator() {
        LocationWarn.stopVibrator()
    }

    fun stopService() {
        TimeTask.cancelClock()
        LocationWarn.stopLocation()
        isRunning.value = false
    }
}