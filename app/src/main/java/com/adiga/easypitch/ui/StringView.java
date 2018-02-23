package com.adiga.easypitch.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.adiga.easypitch.R;

/**
 * Created by dbajj on 2018-02-20.
 */

public class StringView extends View {

    private static final double OFFSET_TOLERANCE = 0.03;
    private static final double SCALE_SENSITIVTITY = 1;

    private float mOffset;

    private float[] mCoordinates;
    private boolean close;
    private Path mStringPath;
    private Paint mStringPaint;
    private Paint mStringPaintHighlight;


    public StringView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.StringView,0,0);

        try {
            mOffset = a.getFloat(R.styleable.StringView_curveOffset,0);
        } finally {
            a.recycle();
        }

        mCoordinates = new float[6];
        mStringPath = new Path();
        mStringPaint = new Paint();
        mStringPaintHighlight = new Paint();
    }

    private void setCoordinates() {
        int heightNoPadding = getLayoutParams().height-getPaddingTop()-getPaddingBottom();
        int widthNoPadding = getLayoutParams().width-getPaddingLeft()-getPaddingRight();
        mCoordinates[0] = getPaddingLeft();
        mCoordinates[1] = (getPaddingTop()+(heightNoPadding/2F));
        mCoordinates[2] = ((getPaddingLeft()+widthNoPadding/2F));
        mCoordinates[3] = findCurveMidpoint();

        mCoordinates[4] = getPaddingLeft()+widthNoPadding;
        mCoordinates[5] = mCoordinates[1];
    }

    private float findCurveMidpoint() {
        if(mOffset > 1F) {
            return 1F;
        } else if (mOffset < -1F) {
            return -1F;
        }

        float midPointY = getPaddingTop()+(getLayoutParams().height-getPaddingBottom()-getPaddingTop())/2F;

        float heightNoPadding = getLayoutParams().height-getPaddingTop()-getPaddingBottom();

        float offsetAdjustment = -1*heightNoPadding*mOffset;


        return midPointY+offsetAdjustment;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setCoordinates();

        mStringPath.reset();

        mStringPath.moveTo(mCoordinates[0],mCoordinates[1]);

        mStringPath.quadTo(mCoordinates[2],mCoordinates[3],mCoordinates[4],mCoordinates[5]);

        mStringPaint.reset();

        mStringPaint.setStyle(Paint.Style.STROKE);
        mStringPaint.setStrokeWidth(10F);
        mStringPaint.setStrokeCap(Paint.Cap.ROUND);
        mStringPaint.setAntiAlias(true);

        if(close) {
            mStringPaintHighlight.reset();
            mStringPaintHighlight.setStyle(Paint.Style.STROKE);
            mStringPaintHighlight.setStrokeCap(Paint.Cap.ROUND);
            mStringPaintHighlight.setStrokeWidth(30F);
            mStringPaintHighlight.setColor(Color.GREEN);
            mStringPaintHighlight.setMaskFilter(new BlurMaskFilter(15,BlurMaskFilter.Blur.NORMAL));

            canvas.drawPath(mStringPath,mStringPaintHighlight);

        }
        canvas.drawPath(mStringPath,mStringPaint);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
    }

    public void setCurveOffset(float f) {
        if(f > 1F) {
            mOffset = 1F;
        } else if (f < -1F) {
            mOffset = -1F;
        } else {
            double scaledOffset = Math.tanh(SCALE_SENSITIVTITY*f);
            mOffset = (float)scaledOffset;
        }

        if(Math.abs(mOffset) <= OFFSET_TOLERANCE) {
            close = true;
        } else {
            close = false;
        }

        invalidate();
    }
}
