package com.beebetter.base.util;

import android.view.View;

public class DataBindingUtil {
    public static int visibleOrInvisible(boolean visible){
        return visible ? View.VISIBLE : View.INVISIBLE;
    }

    public static int visibleOrGone(boolean visible){
        return visible ? View.VISIBLE : View.GONE;
    }
}
