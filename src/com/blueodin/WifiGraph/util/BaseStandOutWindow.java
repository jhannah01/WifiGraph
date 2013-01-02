package com.blueodin.WifiGraph.util;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.StandOutWindow.StandOutLayoutParams;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import com.blueodin.WifiGraph.R;

public abstract class BaseStandOutWindow extends StandOutWindow {

    @Override
    public String getAppName() {
        return getString(R.string.app_name);
    }

    @Override
    public int getAppIcon() {
        return R.drawable.icon;
    }
    protected abstract int getResId(int id);

    protected View inflateLayout(int resId, FrameLayout frame) {
        return inflateLayout(resId, frame, true);
    }
    protected View inflateLayout(int resId, FrameLayout frame, boolean attachToRoot) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(resId, frame, attachToRoot);
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        return new StandOutLayoutParams(id, 650, 200, StandOutLayoutParams.CENTER, StandOutLayoutParams.CENTER);
    }

    @Override
    public int getFlags(int id) {
        return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE;
    }

}
