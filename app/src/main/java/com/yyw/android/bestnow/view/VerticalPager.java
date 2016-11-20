package com.yyw.android.bestnow.view;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;

/**
 * Created by yangyongwen on 16/10/31.
 */

public class VerticalPager extends NestedScrollView {
    private static final int VELCITY_POINT = 270;
    private VelocityTracker velocityTracker;
    ObjectAnimator objectAnimator;
    private float density;

    float lastX;
    float lastY;

    public VerticalPager(Context context) {
        this(context, null);
        density = context.getResources().getDisplayMetrics().density;
    }

    public VerticalPager(Context context, AttributeSet attr) {
        super(context, attr);
        density = context.getResources().getDisplayMetrics().density;
    }

    private Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width=MeasureSpec.getSize(widthMeasureSpec);
//        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
//        int height=MeasureSpec.getSize(heightMeasureSpec);
//        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(parentHeightMeasureSpec), MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;

        int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (objectAnimator != null && objectAnimator.isRunning()) {
                    objectAnimator.cancel();
                }
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                break;

        }

        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getRawX();
        float currentY = event.getRawY();
        int index = event.getActionIndex();
        int action = MotionEventCompat.getActionMasked(event);
        int pointerId = event.getPointerId(index);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (objectAnimator != null && objectAnimator.isRunning()) {
                    objectAnimator.cancel();
                }
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.computeCurrentVelocity(100);
                Log.d("velocity", "Y: " + VelocityTrackerCompat.getYVelocity(velocityTracker, pointerId));

                float velocityY = VelocityTrackerCompat.getYVelocity(velocityTracker, pointerId);
                int halfHeight = getHeight() / 2;
                int scrollY = getScrollY();
                if (velocityY > VELCITY_POINT * density) {
                    smoothScrollInTime(0, 270);
                } else if (velocityY < -VELCITY_POINT * density) {
                    smoothScrollInTime(halfHeight * 2, 270);
                } else if (scrollY < halfHeight) {
                    smoothScrollInTime(0, 270);
                } else {
                    smoothScrollInTime(halfHeight * 2, 270);
                }

                return true;
        }
        lastX = currentX;
        lastY = currentY;
        return super.onTouchEvent(event);
    }

    private void smoothScrollInTime(int destinationY, int time) {
        objectAnimator = ObjectAnimator.ofInt(this, "scrollY", destinationY);
        objectAnimator.setDuration(time);
        objectAnimator.start();
    }

}
