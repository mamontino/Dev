package com.softdesign.devintensive.ui.behaviors;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.softdesign.devintensive.R;

public class HeaderBehavior extends CoordinatorLayout.Behavior<ViewGroup>
{
    private float actionBarSize;
    private float topPaddingMultiply = -1f;
    private float bottomPaddingMultiply = -1f;

    public HeaderBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        actionBarSize = getActionBarSize(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ViewGroup child, View dependency)
    {
        return dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ViewGroup child, View dependency)
    {
        if (topPaddingMultiply < 0)
        {
            topPaddingMultiply = child.getPaddingTop() / dependency.getY();
        }

        if (bottomPaddingMultiply < 0)
        {
            bottomPaddingMultiply = child.getPaddingBottom() / dependency.getY();
        }

        float appBarCurrentHeight = dependency.getY() - actionBarSize;
        int newTopPadding = (int) (appBarCurrentHeight * topPaddingMultiply);
        int newBottomPadding = (int) (appBarCurrentHeight * bottomPaddingMultiply);

        child.setY(dependency.getY());
        child.setPadding(child.getPaddingLeft(), newTopPadding, child.getPaddingRight(), newBottomPadding);
        dependency.setPadding(dependency.getPaddingLeft(), child.getHeight(), dependency.getPaddingRight(), dependency.getPaddingBottom());

        return false;
    }

    private float getActionBarSize(Context context)
    {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        float actionBarSize = styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return actionBarSize;
    }
}