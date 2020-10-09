package com.ooftf.app.ca

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.AppUtils
import com.ooftf.basic.utils.toast
import com.ooftf.master.widget.dialog.ui.OptDialog
import com.tbruyelle.rxpermissions3.RxPermissions

/**
 *
 * @author ooftf
 * @email 994749769@qq.com
 * @date 2020/6/28
 */
/**
 *  tipContent: "为了保证您正常使用此功能，需获取您的相机、储存使用权限，请允许"
 */
fun permissions(
    fragment: Fragment,
    tipContent: String,
    vararg permissions: String,
    action: () -> Unit
) {
    fragment.activity?.let {
        permissions(
            RxPermissions(fragment),
            it,
            tipContent,
            permissions = *permissions,
            action = action
        )
    }


}

fun isAllGranted(rxp: RxPermissions, vararg permissions: String): Boolean {
    permissions.forEach {
        if (!rxp.isGranted(it)) {
            return false
        }
    }
    return true
}

fun permissions(
    rxp: RxPermissions,
    activity: FragmentActivity,
    tipContent: String,
    vararg permissions: String,
    action: (() -> Unit)? = null
) {
    rxp.run {
        if (isAllGranted(this, *permissions)) {
            action?.invoke()
        } else {
            OptDialog(activity).setNegativeText("取消")
                .setPositiveText("去允许").setContentText(
                    tipContent
                ).setPositiveListener { view, dialogPos ->
                    dialogPos.dismiss()
                    shouldShowRequestPermissionRationale(
                        activity, *permissions
                    ).subscribe {
                        if (it) {
                            request(
                                *permissions
                            )
                                .subscribe { granted ->
                                    if (granted) {
                                        action?.invoke()
                                    } else {
                                        toast("获取权限失败")
                                    }

                                }
                        } else {
                            AppUtils.launchAppDetailsSettings()
                        }
                    }

                }.show()
        }
    }

}

fun permissions(
    activity: FragmentActivity,
    tipContent: String,
    vararg permissions: String,
    action: (() -> Unit)? = null
) {
    permissions(
        RxPermissions(activity),
        activity,
        tipContent,
        permissions = *permissions,
        action = action
    )
}
