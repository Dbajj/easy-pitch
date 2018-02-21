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
    public StringView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.StringView,0,0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int leftCoord = 0;
        int topCoord = 0;
        int rightCoord = getLayoutParams().width;
        int bottomCoord = getLayoutParams().height;
        Path p = new Path();
        p.moveTo(leftCoord+10,topCoord+10);
        p.quadTo(((float)(rightCoord-10-leftCoord-10))/2,60,rightCoord-10,0);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20F);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawPath(p,paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
    }
}
