package com.danielme.android.cardview;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import java.lang.reflect.Method;

/**
 * Refactor from http://tech.chitgoks.com/2011/10/29/android-animation-to-expand-collapse-view-its-children/
 */
public class ExpandAndCollapseViewUtil {


    public static void expand(final ViewGroup v, int duration) {
        slide(v, duration, true);
    }

    public static void collapse(final ViewGroup v, int duration) {
        slide(v, duration, false);
    }

    private static void slide(final ViewGroup v, int duration, final boolean expand) {
        try {
            //onmeasure method is protected
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
            m.setAccessible(true);
            m.invoke(
                    v,
                    View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getMeasuredWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
        } catch (Exception e) {
            Log.e("slideAnimation", e.getMessage(), e);
        }

        final int initialHeight = v.getMeasuredHeight();

        if (expand) {
            v.getLayoutParams().height = 0;
        } else {
            v.getLayoutParams().height = initialHeight;
        }
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int newHeight = 0;
                if (expand) {
                    newHeight = (int) (initialHeight * interpolatedTime);
                } else {
                    newHeight = (int) (initialHeight * (1 - interpolatedTime));
                }
                v.getLayoutParams().height = newHeight;
                v.requestLayout();

                if (interpolatedTime == 1 && !expand) {
                    v.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(duration);
        v.startAnimation(a);
    }
}
