package com.muv.technicalplan.expandableLayout;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;


class Utils {
    Utils() {
    }

    static ScrolledParent getScrolledParent(ViewGroup child) {
        ViewParent parent = child.getParent();

        for(int childBetweenParentCount = 0; parent != null; parent = parent.getParent()) {
            if(parent instanceof RecyclerView || parent instanceof AbsListView) {
                ScrolledParent scrolledParent = new ScrolledParent();
                scrolledParent.scrolledView = (ViewGroup)parent;
                scrolledParent.childBetweenParentCount = childBetweenParentCount;
                return scrolledParent;
            }

            ++childBetweenParentCount;
        }

        return null;
    }

    static ValueAnimator createParentAnimator(final View parent, int distance, long duration) {
        ValueAnimator parentAnimator = ValueAnimator.ofInt(new int[]{0, distance});
        parentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int lastDy;
            int dy;

            public void onAnimationUpdate(ValueAnimator animation) {
                this.dy = ((Integer)animation.getAnimatedValue()).intValue() - this.lastDy;
                this.lastDy = ((Integer)animation.getAnimatedValue()).intValue();
                parent.scrollBy(0, this.dy);
            }
        });
        parentAnimator.setDuration(duration);
        return parentAnimator;
    }
}