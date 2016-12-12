package com.yyw.android.bestnow.view.behavior;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangyongwen on 16/11/13.
 */

public class DragBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_SETTLING = 2;
    public static final int STATE_ANCHOR_POINT = 3;
    public static final int STATE_EXPANDED = 4;
    public static final int STATE_COLLAPSED = 5;
    public static final int STATE_HIDDEN = 6;

    /**
     * @hide
     */
    @IntDef({STATE_EXPANDED, STATE_COLLAPSED, STATE_DRAGGING, STATE_ANCHOR_POINT, STATE_SETTLING, STATE_HIDDEN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public interface ScrollCalendarCallback {
        void onStateChanged(@NonNull View appBarLayout, @State int newState);

        void onSlide(@NonNull View appBarLayout, float slideOffset);
    }

    private WeakReference<View> dependencyRef;
    private WeakReference<View> childRef;
    private VelocityTracker velocityTracker;
    private int activePointerId;
    private boolean ignoreEvents;
    private int actionBarHeight;
    private int lastX;
    private int lastY;
    private int lastDy;
    private int minHeight;
    private int maxHeight = -1;
    private
    @State
    int state = STATE_COLLAPSED;
    private List<ScrollCalendarCallback> callbacks;
    private boolean shouldProcessEvent = false;
    private int parentHeight;


    public DragBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        actionBarHeight = getActionBarHeight(context);
        minHeight = actionBarHeight;
    }

    private int getActionBarHeight(Context context) {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            dependencyRef = new WeakReference<View>(dependency);
            childRef = new WeakReference<View>(child);
            return true;
        }
        return false;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
        int dependencyHeight = dependency.getHeight();
//        ViewCompat.offsetTopAndBottom(child,dependencyHeight);
        child.setY(dependencyHeight + dependency.getTop());
        float slideOffset = (float) (dependencyHeight - minHeight) / (float) (maxHeight - minHeight);
        notifyOnSlideToListeners(dependency, slideOffset);
        if (dependencyHeight == minHeight) {
            setIntervalState(STATE_COLLAPSED);
        }
        if (dependencyHeight == maxHeight) {
            setIntervalState(STATE_EXPANDED);
        }
        return true;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
        if (maxHeight == -1) {
            maxHeight = getDependencyHeight();
            int initHeight;
            if (state == STATE_COLLAPSED) {
                initHeight = minHeight;
            } else {
                initHeight = maxHeight;
            }
            parentHeight = parent.getHeight();
            setDependencyHeight(initHeight);
            setChildHeight(parentHeight - minHeight);
            child.setY(initHeight);
            child.requestLayout();
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(ev);
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                activePointerId = MotionEvent.INVALID_POINTER_ID;
                if (ignoreEvents) {
                    ignoreEvents = false;
                    shouldProcessEvent = false;
                    return shouldProcessEvent;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                int dependencyHeight = getDependencyHeight();
                activePointerId = ev.getPointerId(ev.getActionIndex());
                ignoreEvents = activePointerId == MotionEvent.INVALID_POINTER_ID
                        || y <= dependencyHeight;
                break;
        }
        lastX = (int) ev.getX();
        lastY = (int) ev.getY();
        if (getDependencyHeight() > actionBarHeight && !ignoreEvents) {
            shouldProcessEvent = true;
            return shouldProcessEvent;
        }
        shouldProcessEvent = false;
        return shouldProcessEvent;
    }

    private int getDependencyHeight() {
        if (dependencyRef != null && dependencyRef.get() != null) {
            return dependencyRef.get().getHeight();
        }
        return 0;
    }


    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent ev) {
        if (!shouldProcessEvent) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            return true;
        }
        if (action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        int height = getDependencyHeight();
        if (action == MotionEvent.ACTION_MOVE && !ignoreEvents) {
            int dy = (int) ev.getY() - lastY;
            int currentHeight = height + dy;
            setDependencyHeight(currentHeight);
            setIntervalState(STATE_DRAGGING);
            lastDy = dy;
        }
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            setDependencyHeight(STATE_SETTLING);
            int finalHeight;
            if (lastDy < 0) {
                finalHeight = minHeight;
            } else {
                finalHeight = Math.abs(height - minHeight) > Math.abs(height - maxHeight) ?
                        maxHeight :
                        minHeight;
            }
            smoothChangeHeight(height, finalHeight);
        }

        lastX = (int) ev.getX();
        lastY = (int) ev.getY();
        return !ignoreEvents;
    }

    private void setDependencyHeight(int height) {
        height = Math.max(height, minHeight);
        height = Math.min(height, maxHeight);

        View dependency = dependencyRef.get();
        dependency.getLayoutParams().height = height;
        dependency.getParent().requestLayout();
    }

    private void setChildHeight(int height) {
        height = Math.max(height, 0);
        View child = childRef.get();
        child.getLayoutParams().height = height;
        child.getParent().requestLayout();
    }

    private void reset() {
        activePointerId = ViewDragHelper.INVALID_POINTER;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    public void addScrollCallback(ScrollCalendarCallback calendarCallback) {
        if (callbacks == null) {
            callbacks = new ArrayList<>();
        }
        callbacks.add(calendarCallback);
    }

    private void smoothChangeHeight(final int currentHeight, final int finalHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(0, 200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float lastHeight = currentHeight;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float height = currentHeight + (finalHeight - currentHeight) * animation.getAnimatedFraction();
                setDependencyHeight((int) height);
                lastHeight = height;
                if (finalHeight == height) {
                    setIntervalState(finalHeight == minHeight ? STATE_COLLAPSED : STATE_EXPANDED);
                }
            }
        });
        int baseTime = 200;
        int time = baseTime * Math.abs(currentHeight - finalHeight) / (maxHeight - minHeight);
        animator.setDuration(time).start();
    }

    public
    @State
    int getState() {
        return state;
    }

    public void setState(@State int state) {
        if (this.state == state) {
            return;
        }
        this.state = state;
        if (dependencyRef == null || dependencyRef.get() == null) {
            return;
        }
        View dependency = dependencyRef.get();
        int currentHeight = dependency.getHeight();
        int targetHeight;
        if (state == STATE_COLLAPSED) {
            targetHeight = minHeight;
        } else if (state == STATE_EXPANDED) {
            targetHeight = maxHeight;
        } else {
            return;
        }
        setIntervalState(STATE_SETTLING);
        smoothChangeHeight(currentHeight, targetHeight);
    }

    private void setIntervalState(@State int state) {
        if (this.state == state) {
            return;
        }
        this.state = state;
        View child = childRef.get();
        notifyStateChangedToListeners(child, state);
    }

    private void notifyStateChangedToListeners(@NonNull View appBarLayout, @State int newState) {
        if (appBarLayout == null || callbacks == null) {
            return;
        }
        for (ScrollCalendarCallback callback : callbacks) {
            callback.onStateChanged(appBarLayout, newState);
        }
    }

    private void notifyOnSlideToListeners(@NonNull View appBarLayout, float slideOffset) {
        if (appBarLayout == null || callbacks == null) {
            return;
        }
        for (ScrollCalendarCallback callback : callbacks) {
            callback.onSlide(appBarLayout, slideOffset);
        }
    }

    public static <V extends View> DragBehavior<V> from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof DragBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with BottomSheetBehaviorGoogleMapsLike");
        }
        return (DragBehavior<V>) behavior;
    }

}
