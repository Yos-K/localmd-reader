package io.github.yosk.mdlite.presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

final class CustomGestureDrawingView extends View {
    interface Listener {
        void onCustomGestureDrawn(float[] xs, float[] ys);
    }

    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final List<Float> xs = new ArrayList<Float>();
    private final List<Float> ys = new ArrayList<Float>();
    private final Listener listener;
    private final String instruction;

    CustomGestureDrawingView(
            Context context,
            String instruction,
            int backgroundColor,
            int strokeColor,
            int textColor,
            Listener listener) {
        super(context);
        this.instruction = instruction;
        this.listener = listener;
        setBackgroundColor(backgroundColor);
        strokePaint.setColor(strokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(8f);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setColor(textColor);
        textPaint.setTextSize(42f);
        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(instruction, 36f, 72f, textPaint);
        canvas.drawPath(path, strokePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            reset();
            return true;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            reset();
            append(event);
            path.moveTo(event.getX(), event.getY());
            invalidate();
            return true;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            append(event);
            path.lineTo(event.getX(), event.getY());
            invalidate();
            return true;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            append(event);
            path.lineTo(event.getX(), event.getY());
            invalidate();
            listener.onCustomGestureDrawn(toFloatArray(xs), toFloatArray(ys));
            return true;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            reset();
        }
        return true;
    }

    private void append(MotionEvent event) {
        xs.add(Float.valueOf(event.getX()));
        ys.add(Float.valueOf(event.getY()));
    }

    private void reset() {
        xs.clear();
        ys.clear();
        path.reset();
        invalidate();
    }

    private static float[] toFloatArray(List<Float> values) {
        float[] result = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i).floatValue();
        }
        return result;
    }
}
