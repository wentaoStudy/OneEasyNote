package com.wentaostudy.notefundition.ui;

import android.app.Application;

import com.wentaostudy.notefundition.ui.richedittext.utils.ContextUtils;
import com.wentaostudy.notefundition.ui.richedittext.utils.DisplayUtils;


/**
 * @author liye
 * @version 4.1.0
 * @since: 16/4/20 下午6:21
 */
public class RichEditDemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtils.setContext(this);
        DisplayUtils.init(this);
    }
}
