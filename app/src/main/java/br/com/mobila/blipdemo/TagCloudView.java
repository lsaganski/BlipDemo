package br.com.mobila.blipdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TagCloudView extends View {
//    String[] tags = new String[]{"Lemon","Orange","Strawberry","Plum","Pear","Pineapple","Blackberry","Watermelon"};
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public float scroll = 0;
    private float prevY;

    public TagCloudView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        float r = getHeight() / 3;
        paint.setColor(Color.WHITE);
        this.setBackgroundColor(Color.argb(0, 255, 255, 255));
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < Globals.getInstance().tags.size(); i++) {
            float t = i + scroll / getHeight();
            float y = (float) (r * Math.cos(Math.PI * 2 * t / Globals.getInstance().tags.size()));    // parametric circle equation
            float z = (float) (r * Math.sin(Math.PI * 2 * t / Globals.getInstance().tags.size()));
            paint.setTextSize((r + z) / r/2 * 40 + 20);     // magic values, change to something better
            paint.setAlpha((int) ((r + z) / r/2 * 127 + 128));
            canvas.drawText(Globals.getInstance().tags.get(i), getWidth() / 2, getHeight() / 2 + y, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN)
            scroll -= event.getY() - prevY;     // only one plane
        prevY = event.getY();
        invalidate();
        return true;
    }
}