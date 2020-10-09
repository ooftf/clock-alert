package com.ooftf.app.ca;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ooftf.basic.AppHolder;
import com.ooftf.log.JLog;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;


/**
 * @author ooftf
 * @email 994749769@qq.com
 * @date 9/11/2019
 */
public class LocationHelper {
   private static LocationResult bdLocation = null;

    /**
     * 初始化定位参数配置
     *
     * @return
     */

    public static Single<LocationResult> getLocation() {
        final LocationClient locationClient = new LocationClient(AppHolder.INSTANCE.getApp());
        return Single.create((SingleOnSubscribe<LocationResult>) emitter -> {
//定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动

//声明LocationClient类实例并配置定位参数
            LocationClientOption locationOption = new LocationClientOption();
//注册监听函数
            locationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    JLog.e("单次定位回调 " + bdLocation.getLocType());
                    if (bdLocation.getLocType() == 62 || bdLocation.getLocType() == 167 || bdLocation.getLocType() == 162) {
                        JLog.e("单次定位回调  error");
                        emitter.onError(new Throwable("location data error"));
                        locationClient.stop();
                    } else {
                        JLog.e("单次定位回调  success");
                        LocationResult result = new LocationResult();
                        result.setAddress(bdLocation.getAddrStr());
                        result.setLatitude(bdLocation.getLatitude());
                        result.setLongitude(bdLocation.getLongitude());
                        result.speed = bdLocation.getSpeed();
                        LocationHelper.bdLocation = result;
                        emitter.onSuccess(result);
                        locationClient.stop();
                    }

                }
            });
//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            locationOption.setCoorType("bd09ll");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
            locationOption.setScanSpan(0);
//可选，设置是否需要地址信息，默认不需要
            locationOption.setIsNeedAddress(true);
//可选，设置是否需要地址描述
            locationOption.setIsNeedLocationDescribe(true);
//可选，设置是否需要设备方向结果
            locationOption.setNeedDeviceDirect(false);
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            locationOption.setLocationNotify(false);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            //locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            locationOption.setIsNeedLocationDescribe(false);
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            locationOption.setIsNeedLocationPoiList(false);
//可选，默认false，设置是否收集CRASH信息，默认收集
            locationOption.SetIgnoreCacheException(false);
//可选，默认false，设置是否开启Gps定位
            locationOption.setOpenGps(true);
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
            locationOption.setIsNeedAltitude(false);
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
            locationOption.setOpenAutoNotifyMode();
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
            //locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
           /* 1） 签到场景：只进行一次定位返回最接近真实位置的定位结果

            2）运动场景：高精度连续定位，适用于运动类开发者场景

            3）出行场景：高精度连续定位，适用于运动类开发者场景*/
            locationOption.setLocationPurpose(LocationClientOption.BDLocationPurpose.SignIn);
            locationClient.setLocOption(locationOption);
//开始定位
            locationClient.start();
        })
                .timeout(20, TimeUnit.SECONDS)
                .doOnError(throwable -> locationClient.stop())    .onErrorReturn(throwable -> {
                    return bdLocation;
                });


    }

    public static class LocationResult {
        double longitude = 0;
        double latitude = 0;
        String address = "";
        float speed = 0;

        public double getLongitude() {
            return longitude;
        }

        public LocationResult setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public double getLatitude() {
            return latitude;
        }

        public LocationResult setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public String getAddress() {
            return address;
        }

        public LocationResult setAddress(String address) {
            this.address = address;
            return this;
        }

        public float getSpeed() {
            return speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }
    }
}
