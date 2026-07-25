package io.github.yosk.mdlite.presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import io.github.yosk.mdlite.viewer.CustomGestureDrawingLayout;
import java.util.ArrayList;
import java.util.List;

final class CustomGestureDrawingView extends View {
    interface Listener {
        void onCustomGestureDrawn(float[] xs, float[] ys);
        void onCustomGestureDrawingCancelled();
    }

    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final List<Float> xs = new ArrayList<Float>();
    private final List<Float> ys = new ArrayList<Float>();
    private final Listener listener;
    private final String instruction;
    private final String cancelLabel;
    private int systemTopInset;
    private boolean cancelPressed;

    CustomGestureDrawingView(
            Context context,
            String instruction,
            String cancelLabel,
            int systemTopInset,
            int backgroundColor,
            int strokeColor,
            int textColor,
            Listener listener) {
        super(context);
        this.instruction = instruction;
        this.cancelLabel = cancelLabel;
        this.systemTopInset = Math.max(0, systemTopInset);
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
        setContentDescription(instruction + ". " + cancelLabel);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(instruction, 36f,
                CustomGestureDrawingLayout.instructionBaseline(systemTopInset), textPaint);
        canvas.drawText(cancelLabel,
                CustomGestureDrawingLayout.cancelLabelStartX(
                        getWidth(), textPaint.measureText(cancelLabel)),
                CustomGestureDrawingLayout.instructionBaseline(systemTopInset), textPaint);
        canvas.drawPath(path, strokePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            reset();
            return true;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            cancelPressed = CustomGestureDrawingLayout.isCancelTarget(
                    getWidth(), systemTopInset, textPaint.measureText(cancelLabel),
                    event.getX(), event.getY());
            if (cancelPressed) {
                return true;
            }
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
            if (cancelPressed) {
                cancelPressed = false;
                performClick();
                return true;
            }
            append(event);
            path.lineTo(event.getX(), event.getY());
            invalidate();
            listener.onCustomGestureDrawn(toFloatArray(xs), toFloatArray(ys));
            return true;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            cancelPressed = false;
            reset();
        }
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        listener.onCustomGestureDrawingCancelled();
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
