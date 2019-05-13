/*
 * Copyright (c) 2019. Parrot Faurecia Automotive S.A.S. All rights reserved.
 */

package com.example.ts.smoothlineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SmoothLineView extends View {

    public SmoothLineView(Context context) {
        this(context, null);
    }

    public SmoothLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Paint mPaint;
    Path mPath;
    PointF[] mPoints;

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint();
        mPath = new Path();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(1);
        mPoints = initPoint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<PointF> fList = getControlPoints(mPoints);
        mPath.reset();
        for (int i=0;i<mPoints.length-1;i++){
            mPath.moveTo(mPoints[i].x,mPoints[i].y);
            mPath.cubicTo(
                    fList.get(i*2).x,fList.get(i*2).y,
                    fList.get(i*2+1).x,fList.get(i*2+1).y,
                    mPoints[i+1].x,mPoints[i+1].y
            );
        }
        canvas.drawPath(mPath,mPaint);
        for (PointF pointF : mPoints){

            canvas.drawCircle(pointF.x,pointF.y,2,mPaint);
        }
        for (PointF pointF : fList){
            mPaint.setColor(Color.GREEN);
            canvas.drawCircle(pointF.x,pointF.y,2,mPaint);
        }

    }

    private PointF[] initPoint() {
        return mPoints = new PointF[]{
                new PointF(0,200),
                new PointF(50,100),
                new PointF(100,150),
                new PointF(150,100),
                new PointF(200,180),
                new PointF(230,300),
                new PointF(260,80),
                new PointF(300,500),
                new PointF(340,180),
                new PointF(400,130),
                new PointF(450,480),
        };
    }

    private List<PointF> getControlPoints(PointF[] points){
        // 计算斜率 y = kx + b => k = (y - b)/x
        if (points.length < 3){
            return null;
        }
        List<PointF> pointFList = new ArrayList<>();
        float rate = 0.4f;
        //从第一个控制点开始计算
        float cx1 = points[0].x + (points[1].x - points[0].x)*rate;
        float cy1 = points[0].y;
        pointFList.add(new PointF(cx1,cy1));

        for (int i =1;i<points.length-1;i++){
            //第二个点
            float k = (points[i+1].y - points[i-1].y)/(points[i+1].x - points[i-1].x);
            float b = points[i].y - k*points[i].x;
            //左边控制点
            float cxLeft = points[i].x - (points[i].x - points[i-1].x)*rate;
            float cyLeft = k*cxLeft + b;
            pointFList.add(new PointF(cxLeft,cyLeft));
            //右边控制点
            float cxRight = points[i].x + (points[i+1].x - points[i].x)*rate;
            float cyRight = k*cxRight + b;
            pointFList.add(new PointF(cxRight,cyRight));
        }
        //最后一个点
        float cxLast = points[points.length - 1].x - (points[points.length - 1].x - points[points.length - 2].x)*rate;
        float cyLast = points[points.length - 1].y;
        pointFList.add(new PointF(cxLast,cyLast));
        return pointFList;
    }

}
