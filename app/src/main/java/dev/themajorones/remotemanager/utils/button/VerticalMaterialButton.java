package dev.themajorones.remotemanager.utils.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.google.android.material.button.MaterialButton;

public class VerticalMaterialButton extends MaterialButton {
    public VerticalMaterialButton(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    public VerticalMaterialButton(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int wSpec, int hSpec) {
        super.onMeasure(hSpec, wSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int origTextColor = getCurrentTextColor();
        ColorStateList bgTint = getBackgroundTintList();
        int tintColor = bgTint != null
                ? bgTint.getDefaultColor()
                : origTextColor;

        setTextColor(tintColor);
        super.onDraw(canvas);

        setTextColor(origTextColor);
        canvas.save();
        canvas.translate(0, getHeight());
        canvas.rotate(-90);

        Paint paint = getPaint();
        paint.setColor(origTextColor);
        paint.setTextAlign(Paint.Align.CENTER);

        float x = getHeight() * 0.5f;
        float y = (getWidth() - paint.descent() - paint.ascent()) * 0.5f;
        canvas.drawText(getText().toString(), x, y, paint);

        canvas.restore();
    }
}