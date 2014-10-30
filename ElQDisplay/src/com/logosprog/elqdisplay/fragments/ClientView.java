package com.logosprog.elqdisplay.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

/**
 * Created by logosprog on 16.10.2014.
 */
public class ClientView extends View {

    float changingY;
    Typeface font;
    protected int terminal = 0;
    protected int client = 0;

    public ClientView(Context context) {
        super(context);
        changingY = 0;
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
        canvas.drawText("Client" + client + "--> Terminal" + terminal, canvas.getWidth()/2, 200, textPaint);


        /*canvas.drawBitmap(gBall, (canvas.getWidth() / 2), changingY, null);
        if (changingY < canvas.getHeight()) {
            changingY += 10;
        } else {
            changingY = 0;
        }
        Rect middleRect=new Rect();
        middleRect.set(0, 400, canvas.getWidth(), 550);
        Paint ourBlue=new Paint();
        ourBlue.setColor(Color.BLUE);
        canvas.drawRect(middleRect, ourBlue);*/

        //invalidate();
    }
}
