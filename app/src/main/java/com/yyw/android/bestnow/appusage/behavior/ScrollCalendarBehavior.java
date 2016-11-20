package com.yyw.android.bestnow.appusage.behavior;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ScrollCalendarBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
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

    private WeakReference<View> viewRef;
    private int state = STATE_EXPANDED;
    private boolean nestedScrolled = false;
    private int actionBarHeight;
    private int minHeight;
    private int maxHeight = -1;
    private int lastDy;
    private List<ScrollCalendarCallback> callbacks;

    public interface ScrollCalendarCallback {
        void onStateChanged(@NonNull View appBarLayout, @State int newState);

        void onSlide(@NonNull View appBarLayout, float slideOffset);
    }


    public ScrollCalendarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        actionBarHeight = getActionBarHeight(context);
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
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof NestedScrollView || dependency instanceof RecyclerView;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
        if (maxHeight == -1) {
            minHeight = actionBarHeight;
            maxHeight = child.getHeight();
            if (state == STATE_COLLAPSED) {
                child.getLayoutParams().height = minHeight;
                parent.onLayoutChild(child, layoutDirection);
            }
            viewRef = new WeakReference<View>(child);
        }
        return true;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        nestedScrolled = false;
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 &&
                child.getHeight() > actionBarHeight;
    }


    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout,
                                  V child, View target, int dx, int dy, int[] consumed) {
        int currentHeight = child.getHeight();
        int newHeight = currentHeight - dy;
        if (newHeight < minHeight) {
            newHeight = minHeight;
            setIntervalState(STATE_COLLAPSED);
        }else if (newHeight > maxHeight) {
            newHeight = maxHeight;
            setIntervalState(STATE_EXPANDED);
        }else {
            setIntervalState(STATE_DRAGGING);
        }
        if (newHeight != currentHeight) {
            setAppBarLayoutHeight(child, currentHeight, newHeight);
        }
        consumed[1] = dy;
        lastDy = dy;
        nestedScrolled = true;
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout,
                                   final V child, View target) {
        if (!nestedScrolled) {
            return;
        }
        setIntervalState(STATE_SETTLING);
        final int currentHeight = child.getHeight();
        if (currentHeight == minHeight) {
            setIntervalState(STATE_COLLAPSED);
            return;
        }
        if (currentHeight == maxHeight) {
            setIntervalState(STATE_EXPANDED);
            return;
        }
        final int finalHeight;
        if (lastDy <= 0) {
            finalHeight = Math.abs(currentHeight - minHeight) < Math.abs(currentHeight - maxHeight)
                    ? minHeight
                    : maxHeight;
        } else {
            finalHeight = minHeight;
        }
        smoothChangeHeight(child,currentHeight,finalHeight);
        nestedScrolled = false;
    }

    private void smoothChangeHeight(final View child,final int currentHeight,final int finalHeight){
        ValueAnimator animator = ValueAnimator.ofInt(0, 200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float lastHeight = currentHeight;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float height = currentHeight + (finalHeight - currentHeight) * animation.getAnimatedFraction();
                setAppBarLayoutHeight(child, (int) lastHeight, (int) height);
                lastHeight = height;
                if (finalHeight == height) {
                    setIntervalState(finalHeight == minHeight ? STATE_COLLAPSED : STATE_EXPANDED);
                }
            }
        });
        animator.setDuration(300).start();
    }

    public @State int getState(){
        return state;
    }

    public void setState(@State int state){
        if (this.state==state){
            return;
        }
        this.state=state;
        if (viewRef==null||viewRef.get()==null){
            return;
        }
        View child=viewRef.get();
        int currentHeight=child.getHeight();
        int targetHeight;
        if (state==STATE_COLLAPSED){
            targetHeight=minHeight;
        }else if (state==STATE_EXPANDED){
            targetHeight=maxHeight;
        }else {
            return;
        }
        setIntervalState(STATE_SETTLING);
        smoothChangeHeight(child,currentHeight,targetHeight);
    }

    public void addScrollCallback(ScrollCalendarCallback calendarCallback) {
        if (callbacks == null) {
            callbacks = new ArrayList<>();
        }
        callbacks.add(calendarCallback);
    }

    private void setIntervalState(@State int state) {
        if (this.state == state) {
            return;
        }
        this.state = state;
        View child = viewRef.get();
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

    private void setAppBarLayoutHeight(View appBarLayout, int currentHeight, int newHeight) {
        int dy = currentHeight - newHeight;
        if (dy == 0) {
            return;
        } else {
            appBarLayout.getLayoutParams().height = newHeight;
            appBarLayout.requestLayout();
            notifyOnSlideToListeners(appBarLayout, (float)(newHeight-minHeight) / (float)(maxHeight - minHeight));
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

    @SuppressWarnings("unchecked")
    public static <V extends View> ScrollCalendarBehavior<V> from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof ScrollCalendarBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with BottomSheetBehaviorGoogleMapsLike");
        }
        return (ScrollCalendarBehavior<V>) behavior;
    }

}
