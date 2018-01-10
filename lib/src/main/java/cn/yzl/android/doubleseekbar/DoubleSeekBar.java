package cn.yzl.android.doubleseekbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by YZL on 2018/1/9.
 */
public class DoubleSeekBar extends View {
    //1K
    int minValue = 1;
    //100K
    int maxValue = 100;

    int curMinValue = minValue;

    int curMaxValue = maxValue;

    //线条宽度
    int lineWidth;

    /**
     * 默认线条颜色
     */
    int colorDefault = Color.parseColor("#E5E6E6");

    /**
     * 选中线条颜色
     */
    int colorSel = Color.parseColor("#4294F7");

    int colorText = Color.WHITE;


    Bitmap bgNumbBitmap;
    Bitmap markBitmap;

    Paint numbPaint;

    Paint defaultLinePaint;
    Paint linePaint;

    Paint bitmapPaint;
    private Rect markerSrcRect;
    private Rect bgNumbSrcRect;


    public MoveListener moveListener;
    public ChangedListener changedListener;


    public DoubleSeekBar(Context context) {
        super(context);
        init();
    }

    public DoubleSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DoubleSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        lineWidth = dp2px(2);

        BitmapDrawable bm = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.bg_numb);
        bgNumbBitmap = bm.getBitmap();
        bgNumbSrcRect = new Rect(0, 0, bm.getIntrinsicWidth(), bm.getIntrinsicHeight());

        bm = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.logo_marker);
        markBitmap = bm.getBitmap();
        markerSrcRect = new Rect(0, 0, bm.getIntrinsicWidth(), bm.getIntrinsicHeight());


        numbPaint = new Paint();
        numbPaint.setDither(true);
        numbPaint.setAntiAlias(true);
        numbPaint.setColor(colorText);
        numbPaint.setTextSize(dp2px(14));

        linePaint = new Paint();
        linePaint.setDither(true);
        linePaint.setAntiAlias(true);
        linePaint.setColor(colorSel);
        linePaint.setStrokeWidth(lineWidth);

        defaultLinePaint = new Paint();
        defaultLinePaint.setStrokeWidth(lineWidth);
        defaultLinePaint.setDither(true);
        defaultLinePaint.setAntiAlias(true);
        defaultLinePaint.setColor(colorDefault);

        bitmapPaint = new Paint();
        bitmapPaint.setDither(true);
        bitmapPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            //预览的时候不绘制
            return;
        }
        super.onDraw(canvas);
        drawLine(canvas);
    }

    //在drawLine中计算
    //线段长度
    float lineLength = 0F;
    //线段开始X
    float lineStartX;
    //线段开始Y
    float lineStartY;

    //记录当前 最大最小操作点位置
    Rect leftMoveRect;
    Rect rightMoveRect;

    /**
     * 一个数值所占用的px宽度
     */
    float spaceValuePx;

    private void drawLine(Canvas canvas) {
        lineLength = getWidth();
        //为最左最右 留出文字和mark空间
        lineStartX = dp2px(20);
        lineLength -= lineStartX * 2;
        spaceValuePx = lineLength / 100;
        //减去1刻度,因为实际上是 99段
        lineStartX += spaceValuePx / 2;
        lineLength -= spaceValuePx;
        float height = getHeight();

        lineStartY = height / 2 - lineWidth / 2;

        canvas.drawLine(lineStartX, lineStartY, lineStartX + lineLength, lineStartY, defaultLinePaint);
        float selStartX = lineStartX + (curMinValue - 1) * spaceValuePx;

        float selEndX = lineStartX + (curMaxValue - 1) * spaceValuePx;
        canvas.drawLine(selStartX, lineStartY, selEndX, lineStartY, linePaint);
        //画指示点
        float leftStartX = selStartX - lineWidth / 2;
        float rightStartX = selEndX + lineWidth / 2;
        float pointStartY = (lineStartY + lineWidth / 2) - dp2px(10) / 2;
        float pointEndY = (lineStartY) + dp2px(10) / 2;
        canvas.drawLine(leftStartX, pointStartY, leftStartX, pointEndY, linePaint);
        canvas.drawLine(rightStartX, pointStartY, rightStartX, pointEndY, linePaint);

        // 画下面指示器
        float markerMarginTop = dp2px(5);
        float[] size = new float[]{dp2px(22), dp2px(26)};
        Rect leftTargetRect = new Rect((int) (selStartX - size[0] / 2), (int) (pointEndY + markerMarginTop),
                (int) (selStartX + size[0] / 2),
                (int) (pointEndY + markerMarginTop + size[1]));
        {
            //可触摸区域
            leftMoveRect = new Rect(leftTargetRect);
            leftMoveRect.left = leftMoveRect.left + 10;
            leftMoveRect.right = leftMoveRect.right + 10;
            leftMoveRect.bottom = getHeight();
        }

        canvas.drawBitmap(markBitmap, markerSrcRect, leftTargetRect, bitmapPaint);

        Rect rightTargetRect = new Rect((int) (selEndX + lineWidth / 2 - size[0] / 2), (int) (pointEndY + markerMarginTop),
                (int) (selEndX + lineWidth / 2 + size[0] / 2),
                (int) (pointEndY + markerMarginTop + size[1]));
        {   //可触摸区域
            rightMoveRect = new Rect(rightTargetRect);
            rightMoveRect.left = rightMoveRect.left + 10;
            rightMoveRect.right = rightMoveRect.right + 10;
            rightMoveRect.bottom = getHeight();
        }


        canvas.drawBitmap(markBitmap, markerSrcRect, rightTargetRect, bitmapPaint);
        //画文字
        size[0] = dp2px(38);
        size[1] = dp2px(25);
        //画背景
        Rect leftNumbRect = new Rect((int) (selStartX - size[0] / 2),
                (int) (pointStartY - markerMarginTop - size[1]),
                (int) (selStartX + size[0] / 2),
                (int) (pointStartY - markerMarginTop)
        );
        canvas.drawBitmap(bgNumbBitmap, bgNumbSrcRect, leftNumbRect, bitmapPaint);

        Rect rightNumbRect = new Rect((int) (selEndX + lineWidth / 2 - size[0] / 2),
                (int) (pointStartY - markerMarginTop - size[1]),
                (int) (selEndX + lineWidth / 2 + size[0] / 2),
                (int) (pointStartY - markerMarginTop)
        );
        canvas.drawBitmap(bgNumbBitmap, bgNumbSrcRect, rightNumbRect, bitmapPaint);
        //画数值
        float v = numbPaint.measureText(curMinValue + "K");

        canvas.drawText(curMinValue + "K", leftNumbRect.centerX() - v / 2, leftNumbRect.centerY() + dp2px(3), numbPaint);

        v = numbPaint.measureText(curMaxValue + "K");
        canvas.drawText(curMaxValue + "K", rightNumbRect.centerX() - v / 2, rightNumbRect.centerY() + dp2px(3), numbPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            return super.onTouchEvent(event);
        }
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (checkEventType(event)) {
                        return true;
                    } else {
                        return super.onTouchEvent(event);
                    }
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    if (eventType == 1) {
                        int numb = (int) ((x - lineStartX) / spaceValuePx);
                        if (numb != curMaxValue) {
                            if (numb <= curMinValue) {
                                return true;
                            }
                            if (numb > maxValue) {
                                return true;
                            }
                            curMaxValue = numb;
                            if (moveListener != null) {
                                moveListener.move(curMinValue, curMaxValue);
                            }
                            invalidate();
                        }
                    } else {
                        int numb = (int) ((x - lineStartX) / spaceValuePx);
                        if (numb != curMaxValue) {
                            if (numb >= curMaxValue) {
                                return true;
                            }
                            if (numb < minValue) {
                                return true;
                            }
                            curMinValue = numb;
                            if (moveListener != null) {
                                moveListener.move(curMinValue, curMaxValue);
                            }
                            invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (changedListener != null) {
                        changedListener.changed(curMinValue, curMaxValue);
                    }
                    break;
            }
            return super.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    int eventType;

    /**
     * @param event
     * @return -1 没有击中操作点,0 最小值,1最大值
     */
    public boolean checkEventType(MotionEvent event) {
        if (rightMoveRect.contains((int) event.getX() + 1, (int) event.getY() + 1)) {
            eventType = 1;
            return true;
        }

        if (leftMoveRect.contains((int) event.getX() + 1, (int) event.getY() + 1)) {
            eventType = 0;
            return true;
        }
        eventType = -1;
        return false;
    }

    public void setCurMinValue(int curMinValue) {
        this.curMinValue = curMinValue;
        postInvalidate();
    }

    public void setCurMaxValue(int curMaxValue) {
        this.curMaxValue = curMaxValue;
        postInvalidate();
    }




    public int getCurMaxValue() {
        return curMaxValue;
    }

    public int getCurMinValue() {
        return curMinValue;
    }

    public void setMoveListener(MoveListener moveListener) {
        this.moveListener = moveListener;
    }

    public void setChangedListener(ChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    /**
     * 移动中
     */
    interface MoveListener {
        void move(int min, int max);
    }

    /**
     * 结束滑动后
     */
    interface ChangedListener {
        void changed(int min, int max);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED
                || MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            //wrap_content 的时候 处理一下,设置为最低高度
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(dp2px(80), MeasureSpec.EXACTLY));
            return;
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        //防止绘制不全,最低高度为80dp
        if (getMeasuredHeight() < dp2px(80)) {
            setMeasuredDimension(getMeasuredWidth(), dp2px(80));
        }
    }

    public int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getContext().getResources().getDisplayMetrics());
    }
}
