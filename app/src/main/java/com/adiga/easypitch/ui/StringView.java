package com.adiga.easypitch.ui;

import android.content.Context;
import android.content.res.TypedArray;
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

    private float mOffset;

    private float[] mCoordinates;

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

        float offsetAdjustment = heightNoPadding*mOffset;


        return midPointY+offsetAdjustment;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setCoordinates();

        Path p = new Path();
        p.moveTo(mCoordinates[0],mCoordinates[1]);

        p.quadTo(mCoordinates[2],mCoordinates[3],mCoordinates[4],mCoordinates[5]);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5F);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawPath(p,paint);
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
            mOffset = f;
        }

        invalidate();
    }
}
