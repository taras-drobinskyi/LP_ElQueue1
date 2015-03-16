/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

/**
 * Created by forando on 13.03.15.
 */
public class BottomLineView extends View {

    Typeface font;
    protected int restOfClients = 0;

    public BottomLineView(Context context) {
        super(context);
        font=Typeface.createFromAsset(context.getAssets(), "G-Unit.ttf");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);


        Paint textPaint =new Paint();
        textPaint.setARGB(50, 254, 10, 50);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(50);
        textPaint.setTypeface(font);
        canvas.drawText("Client" + restOfClients, canvas.getWidth()/2, 200, textPaint);


        /*
        Rect middleRect=new Rect();
        middleRect.set(0, 400, canvas.getWidth(), 550);
        Paint ourBlue=new Paint();
        ourBlue.setColor(Color.BLUE);
        canvas.drawRect(middleRect, ourBlue);*/
    }
}
