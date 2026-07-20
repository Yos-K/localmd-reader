package io.github.yosk.mdlite.presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

final class GesturePreviewView extends View {
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final int kind;
    private long startedAt;

    GesturePreviewView(Context context, int kind, int lineColor, int dotColor) {
        super(context);
        this.kind = kind;
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5f);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        dotPaint.setColor(dotColor);
        dotPaint.setStyle(Paint.Style.FILL);
        startedAt = System.currentTimeMillis();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        drawStaticPath(width, height);
        canvas.drawPath(path, linePaint);
        float[] point = animatedPoint(width, height);
        canvas.drawCircle(point[0], point[1], animatedDotRadius(), dotPaint);
        postInvalidateOnAnimation();
    }

    private void drawStaticPath(float width, float height) {
        path.reset();
        if (kind == GesturePreviewKind.DOUBLE_TAP) {
            path.addCircle(width * 0.5f, height * 0.5f, Math.min(width, height) * 0.18f, Path.Direction.CW);
            return;
        }
        if (kind == GesturePreviewKind.CIRCLE) {
            path.addCircle(width * 0.5f, height * 0.5f, Math.min(width, height) * 0.28f, Path.Direction.CW);
            return;
        }
        if (kind == GesturePreviewKind.CUSTOM) {
            float[][] points = customPoints(width, height);
            path.moveTo(points[0][0], points[0][1]);
            path.cubicTo(points[1][0], points[1][1], points[2][0], points[2][1], points[3][0], points[3][1]);
            path.lineTo(points[4][0], points[4][1]);
            return;
        }
        float[][] points = pointsFor(width, height);
        path.moveTo(points[0][0], points[0][1]);
        path.lineTo(points[1][0], points[1][1]);
        path.lineTo(points[2][0], points[2][1]);
    }

    private float[] animatedPoint(float width, float height) {
        float progress = progress();
        if (kind == GesturePreviewKind.DOUBLE_TAP) {
            return new float[] { width * 0.5f, height * 0.5f };
        }
        if (kind == GesturePreviewKind.CIRCLE) {
            double angle = (Math.PI * 2d * progress) - (Math.PI / 2d);
            float radius = Math.min(width, height) * 0.28f;
            return new float[] {
                (width * 0.5f) + ((float) Math.cos(angle) * radius),
                (height * 0.5f) + ((float) Math.sin(angle) * radius)
            };
        }
        if (kind == GesturePreviewKind.CUSTOM) {
            return pointOnPolyline(customPoints(width, height), progress);
        }
        return pointOnPolyline(pointsFor(width, height), progress);
    }

    private float animatedDotRadius() {
        if (kind != GesturePreviewKind.DOUBLE_TAP) {
            return 6f;
        }
        float progress = progress();
        float firstTap = pulse(progress, 0.18f);
        float secondTap = pulse(progress, 0.58f);
        return 5f + (8f * Math.max(firstTap, secondTap));
    }

    private float pulse(float progress, float center) {
        float distance = Math.abs(progress - center);
        return Math.max(0f, 1f - (distance / 0.12f));
    }

    private float progress() {
        long elapsed = (System.currentTimeMillis() - startedAt) % 1200L;
        return elapsed / 1200f;
    }

    private float[][] pointsFor(float width, float height) {
        if (kind == GesturePreviewKind.CHEVRON_LEFT) {
            return new float[][] {
                { width * 0.70f, height * 0.22f },
                { width * 0.28f, height * 0.50f },
                { width * 0.70f, height * 0.78f }
            };
        }
        if (kind == GesturePreviewKind.CHEVRON_RIGHT) {
            return new float[][] {
                { width * 0.30f, height * 0.22f },
                { width * 0.72f, height * 0.50f },
                { width * 0.30f, height * 0.78f }
            };
        }
        if (kind == GesturePreviewKind.CHEVRON_UP) {
            return new float[][] {
                { width * 0.22f, height * 0.70f },
                { width * 0.50f, height * 0.28f },
                { width * 0.78f, height * 0.70f }
            };
        }
        if (kind == GesturePreviewKind.CHEVRON_DOWN) {
            return new float[][] {
                { width * 0.22f, height * 0.30f },
                { width * 0.50f, height * 0.72f },
                { width * 0.78f, height * 0.30f }
            };
        }
        return new float[][] {
            { width * 0.24f, height * 0.65f },
            { width * 0.42f, height * 0.30f },
            { width * 0.76f, height * 0.58f }
        };
    }

    private float[][] customPoints(float width, float height) {
        return new float[][] {
            { width * 0.22f, height * 0.62f },
            { width * 0.36f, height * 0.18f },
            { width * 0.54f, height * 0.82f },
            { width * 0.72f, height * 0.34f },
            { width * 0.82f, height * 0.68f }
        };
    }

    private float[] pointOnPolyline(float[][] points, float progress) {
        float scaled = progress * (points.length - 1);
        int segment = Math.min((int) scaled, points.length - 2);
        float local = scaled - segment;
        return new float[] {
            points[segment][0] + ((points[segment + 1][0] - points[segment][0]) * local),
            points[segment][1] + ((points[segment + 1][1] - points[segment][1]) * local)
        };
    }
}
