package com.yyw.android.bestnow.ui.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.yyw.android.bestnow.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samsung on 2016/11/2.
 */

public class CirclePicker extends View {
    private Paint circlePaint;
    private Paint arcPaint;
    private Paint pointPaint;
    private Point pickerPosition;
    private boolean isPickerPositionInit;
    private boolean isDragging = false;
    private int divideDegree;
    private float sweepAngle;
    private float circleRadius;
    private int height;
    private int width;

    private float unCoveredPathWidth;
    private float coveredPathWidth;
    private int unCoveredPathColor;
    private int coveredPathColor;
    private float pickerRadius;
    private int divideCounts;

    private long cancelTime;

    List<OnPickListener> listeners;

    public CirclePicker(Context context) {
        super(context);
    }

    public CirclePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CirclePicker(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        listeners = new ArrayList<>();
        initParams(context, attributeSet);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(unCoveredPathColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(unCoveredPathWidth);

        arcPaint = new Paint();
        arcPaint.setColor(coveredPathColor);
        arcPaint.setStrokeJoin(Paint.Join.ROUND);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(coveredPathWidth);

        pointPaint = new Paint();
        pointPaint.setColor(coveredPathColor);
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(coveredPathColor);

        pickerPosition = new Point();
        isPickerPositionInit = false;

        divideDegree = 360 / divideCounts;
        sweepAngle = 0;
    }

    private void initParams(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CirclePicker, 0, 0);
        try {
            unCoveredPathColor = a.getColor(R.styleable.CirclePicker_unCoveredPathColor, Color.BLACK);
            coveredPathColor = a.getColor(R.styleable.CirclePicker_coveredPathColor, Color.rgb(0x66, 0xff, 0x66));
            unCoveredPathWidth = a.getDimension(R.styleable.CirclePicker_unCoveredPathWidth, 37);
            coveredPathWidth = a.getDimension(R.styleable.CirclePicker_coveredPathWidth, 38);
            pickerRadius = a.getDimension(R.styleable.CirclePicker_pickerRadius, 40);
            divideCounts = a.getInt(R.styleable.CirclePicker_divideCounts, 36);
//            if (pickerRadius * 2 < coveredPathWidth || unCoveredPathWidth > coveredPathWidth) {
//                throw new IllegalStateException("size error");
//            }
        } finally {
            a.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initSize();
        canvas.translate(width / 2, height / 2);
        Path circlePath = new Path();
        circlePath.addCircle(0, 0, circleRadius, Path.Direction.CW);
        canvas.drawPath(circlePath, circlePaint);
        canvas.drawArc(-circleRadius, -circleRadius, circleRadius, circleRadius, -90, sweepAngle, false, arcPaint);
        canvas.drawCircle(pickerPosition.x - width / 2, pickerPosition.y - height / 2, pickerRadius, pointPaint);
    }

    private void initSize(){
        height=getHeight();
        width=getWidth();
        circleRadius=Math.min(circleRadius,height);
        circleRadius=Math.min(circleRadius,width);
        int size=Math.min(height,width)/2;
        circleRadius=size-pickerRadius;
        if (!isPickerPositionInit){
            isPickerPositionInit=true;
            pickerPosition.x=width/2;
            pickerPosition.y=height/2-(int)circleRadius;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                double distance = Math.pow((double) (x - pickerPosition.x), 2) +
                        Math.pow((double) (y - pickerPosition.y), 2);
                if (distance < pickerRadius * pickerRadius * 4) {
                    isDragging = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                } else {
                    isDragging = false;
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    x -= width / 2;
                    y -= height / 2;
                    float currentAngle = computeSweepAngle(x, y);
                    if (Math.abs(currentAngle - sweepAngle) < 180) {
                        sweepAngle = constrainAngle(currentAngle);
                        computePickerPosition(sweepAngle);
                        invalidate();
                        onPick(sweepAngle);
                    } else if (sweepAngle > 180 && currentAngle >= 0 && currentAngle < 90) {
                        sweepAngle = 360;
                        computePickerPosition(sweepAngle);
                        invalidate();
                        onPick(sweepAngle);
                    } else if (sweepAngle > 0 && sweepAngle < 90 && currentAngle > 270) {
                        sweepAngle = 0;
                        computePickerPosition(sweepAngle);
                        invalidate();
                        onPick(sweepAngle);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                isDragging = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    private float computeSweepAngle(float x, float y) {
        double angle;
        float z2 = x * x + y * y;
        if (x > 0 && y <= 0) {
            angle = Math.asin(Math.sqrt(x * x / z2));
        } else if (x > 0 && y > 0) {
            angle = Math.PI - Math.asin(Math.sqrt(x * x / z2));
        } else if (x <= 0 && y >= 0) {
            angle = Math.PI + Math.asin(Math.sqrt(x * x / z2));
        } else {
            angle = Math.PI * 2 - Math.asin(Math.sqrt(x * x / z2));
        }
        return (float) (angle / 2 / Math.PI * 360);
    }

    private void computePickerPosition(double sweepAngle) {
        double angle = sweepAngle / 180 * Math.PI;
        double radius = circleRadius;
        double x, y;
        if (sweepAngle > 0 && sweepAngle <= 90) {
            x = Math.sin(angle) * radius;
            y = -Math.cos(angle) * radius;
        } else if (sweepAngle > 90 && sweepAngle < 180) {
            angle = Math.PI - angle;
            x = Math.sin(angle) * radius;
            y = Math.cos(angle) * radius;
        } else if (sweepAngle >= 180 && sweepAngle <= 270) {
            angle = angle - Math.PI;
            x = -Math.sin(angle) * radius;
            y = Math.cos(angle) * radius;
        } else {
            angle = Math.PI * 2 - angle;
            x = -Math.sin(angle) * radius;
            y = -Math.cos(angle) * radius;
        }
        pickerPosition.x = (int) x + width / 2;
        pickerPosition.y = (int) y + height / 2;
    }

    private float constrainAngle(float angle) {
        int count = (int) (angle / divideDegree);
        if (angle % divideDegree > divideDegree / 2) {
            count++;
        }
        return count * divideDegree;
    }

    public interface OnPickListener {
        void onPick(float angle);
    }

    private void onPick(float angle) {
        for (OnPickListener listener : listeners) {
            listener.onPick(angle);
        }
    }

    public void addOnPickListener(OnPickListener listener) {
        listeners.add(listener);
    }

}