package com.ooftf.app.ca;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author ooftf
 * @email 994749769@qq.com
 * @date 2020/10/9
 */
class ViewHelper {
    public void replace(View view, ViewGroup wrapper, ViewGroup.LayoutParams params) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null) {
            wrapper.setLayoutParams(lp);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            int index = parent.indexOfChild(view);
            parent.removeView(view);
            wrapper.setId(view.getId());
            parent.addView(wrapper, index);
        }
        wrapper.addView(view, params);
    }
}
