/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package com.logosprog.elqdisplay.fragments.tableutils;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * Created by forando on 03.11.14.
 */
public class TextDrawable extends Drawable {

    private String text;
    private final Paint paint;
    Rect bounds;

    private float x = 0, y = 0;

    public TextDrawable(String text) {

        this.text = text;

        this.paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50f);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setShadowLayer(12f, 10f, 10f, Color.DKGRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);
        bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

    }

    public void setText(String value){
        this.text = value;
        paint.getTextBounds(text, 0, text.length(), bounds);
    }
    public String getText(){return this.text;}
    public void setX(float value) {
        x = value;
    }
    public float getX() {
        return x;
    }
    public void setY(float value) {
        y = value;
    }
    public float getY() {
        return y;
    }
    public float getHeight() {
        return 22f;
    }
    public void setVisible(boolean value){setVisible(value, false);}
    public boolean getVisible(){return isVisible();}
    public void setFontSize(float val){
        this.paint.setTextSize(val);
        paint.getTextBounds(text, 0, text.length(), bounds);
    }
    public float getFontSize(){return this.paint.getTextSize();}

    /**
     *
     * @return the width (int) of paint with specified text string
     */
    public int getTextWidth() {
        //paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.left + bounds.width();
    }

    @Override
    public void draw(Canvas canvas) {
        if (isVisible()) {
            canvas.drawText(text, 0, 0, paint);
            //canvas.drawRect(bounds, paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
