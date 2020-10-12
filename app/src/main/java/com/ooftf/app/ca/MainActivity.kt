package com.ooftf.app.ca

import android.os.Bundle
import com.baidu.mapapi.SDKInitializer
import com.ooftf.app.ca.databinding.ActivityMainBinding
import com.ooftf.arch.frame.mvvm.activity.BaseMvvmActivity
import com.ooftf.basic.AppHolder
import com.ooftf.log.JLog

class MainActivity : BaseMvvmActivity<ActivityMainBinding,MainViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SDKInitializer.initialize(AppHolder.app)
        viewModel.data
        JLog.e(this.resources.displayMetrics.widthPixels)

    }

}