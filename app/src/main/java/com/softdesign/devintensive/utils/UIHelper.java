package com.softdesign.devintensive.utils;


import android.content.Context;
import android.util.TypedValue;

public class UIHelper
{
    private static Context context = DevIntensiveApplication.getContext();

    public static int getStatusBarHeight()
    {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bat_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getActionBarHeight()
    {
        int result = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            result = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return result;
    }

    public static int lerp(int start, int end, float friction)
    {
        return (int) (start + (end - start) * friction);
    }

    public static float currentFriction(int start, int end, int currentValue)
    {
        return (float) (currentValue - start) / (end - start);
    }
}
