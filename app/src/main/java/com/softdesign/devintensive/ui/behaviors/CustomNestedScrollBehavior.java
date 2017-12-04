package com.softdesign.devintensive.ui.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.UIHelper;

public class CustomNestedScrollBehavior extends AppBarLayout.ScrollingViewBehavior
{
    private final int maxAppbarHeight;
    private final int minAppbarHeight;
    private final int maxUserInfoHeight;

    public CustomNestedScrollBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        minAppbarHeight = UIHelper.getStatusBarHeight() + UIHelper.getActionBarHeight(); //80dp
        maxAppbarHeight = context.getResources().getDimensionPixelSize(R.dimen.profile_image_size);
        maxUserInfoHeight = context.getResources().getDimensionPixelOffset(R.dimen.spacing_larger_104dp);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, final View dependency)
    {
        float friction = UIHelper.currentFriction(minAppbarHeight, maxAppbarHeight, dependency.getBottom());
        int offsetY = UIHelper.lerp(maxUserInfoHeight / 2, maxUserInfoHeight, friction);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.topMargin = offsetY;
        child.setLayoutParams(lp);

        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency)
    {
        return dependency instanceof AppBarLayout;
    }
}
