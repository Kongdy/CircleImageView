package com.project.kongdy.circleimageview.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * @author kongdy
 *         on 2016/5/24
 */
public class CircleImageView extends ImageView {

    private Bitmap ImageSrc;

    private BitmapShader mBitmapShader;
    private Matrix shaderMatrix;

    private Paint defaultPaint;
    private Paint circlePaint;
    private Paint innerPaint;

    private int centerX = -1;
    private int centerY = -1;
    private int radius = -1;

    private int mBitmapWidth;
    private int mBitmapHeight;
    private int mWidth;
    private int mHeight;
    /**
     * 是否绘制外环
     */
    private boolean drawExternalCircle = false;

    private Rect mDrawableRect;
    /**
     * 内环背景色
     */
    private int innerCircleColor;
    /**
     * 外环背景色
     */
    private int externalCircleColor;

    private int cellSpace;

    private boolean setInnerColor = false;
    private boolean setExternalColor = false;
    /**
     * 向外部提供一个准确的左上角坐标
     */
    private Point leftTopPoint;

    public CircleImageView(Context context) {
        this(context,null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTools();
    }

    private void initTools() {
        super.setScaleType(ScaleType.CENTER_CROP);
        defaultPaint = new Paint();
        circlePaint = new Paint();
        innerPaint = new Paint();
        shaderMatrix = new Matrix();
        mDrawableRect = new Rect();

        leftTopPoint = new Point();

        defaultPaint.setAntiAlias(true);
        circlePaint.setAntiAlias(true);
        innerPaint.setAntiAlias(true);

        defaultPaint.setDither(true);

        circlePaint.setStyle(Style.STROKE);

        circlePaint.setStrokeWidth(getRawSize(TypedValue.COMPLEX_UNIT_DIP, 1));
    }


    private void updateShaderMatrix() {
        shaderMatrix.set(null);
        mBitmapWidth = ImageSrc.getWidth();
        mBitmapHeight = ImageSrc.getHeight();
        float scale = 0;
        float dx = 0; // x轴偏移
        float dy = 0; // y轴偏移
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }
        shaderMatrix.setScale(scale, scale);
        shaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left + (mWidth - 2 * radius) / 2, (int) (dy + 0.5f) + mDrawableRect.top
                + (mHeight - 2 * radius) / 2);
        mBitmapShader.setLocalMatrix(shaderMatrix);
    }

    public void setMyImageRes(int resId) {
        ImageSrc = BitmapFactory.decodeResource(getResources(), resId);
        setImageBitmap(ImageSrc);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (ImageSrc == null || ImageSrc != getImageBitmap(getDrawable())) {
            ImageSrc = getImageBitmap(getDrawable());
            if (ImageSrc == null)
                return;
            // 拉伸
            mBitmapShader = new BitmapShader(ImageSrc, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mDrawableRect.set(0, 0, 2 * radius, 2 * radius);
            updateShaderMatrix();
            defaultPaint.setShader(mBitmapShader);
        }
        canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), defaultPaint
                , Canvas.ALL_SAVE_FLAG);
        canvas.drawCircle(centerX, centerY, radius + getRawSize(TypedValue.COMPLEX_UNIT_DIP, 1), innerPaint); // 1像素的内环
        if (drawExternalCircle) {
            canvas.drawCircle(centerX, centerY, radius + cellSpace, circlePaint);// 1像素的外环
        }
        canvas.drawCircle(centerX, centerY, radius, defaultPaint);

        canvas.restore();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        centerX = mWidth / 2;
        centerY = mHeight / 2;
        int orgRadius = centerX > centerY ? centerY : centerX;
        cellSpace = orgRadius / 15 < 1 ? 1 : orgRadius / 15;
        radius = (int) (orgRadius - cellSpace - circlePaint.getStrokeWidth() -
                getRawSize(TypedValue.COMPLEX_UNIT_DIP, 1));
        innerPaint.setColor(getInnerCircleColor());
        circlePaint.setColor(getExternalCircleColor());
        leftTopPoint.x = (int) (centerX + Math.sin(Math.toRadians(225f)) * radius) / 2;
        leftTopPoint.y = (int) (centerY + Math.cos(Math.toRadians(225f)) * radius) / 2;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        ImageSrc = bm;
    }

    public Bitmap getImageBitmap(Drawable dra) {
        if (dra == null) {
            return null;
        }
        final int width = dra.getIntrinsicWidth();
        final int height = dra.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height
                , dra.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        dra.setBounds(0, 0, width, height);
        dra.draw(canvas);
        return bitmap;
    }

    /**
     * 关闭圆环
     */
    public void closeExternalCircle() {
        this.drawExternalCircle = false;
        invalidate();
    }

    public int getInnerCircleColor() {
        if (setInnerColor) {
            return innerCircleColor;
        }
        return Color.WHITE;
    }

    public void setInnerCircleColor(int innerCircleColor) {
        this.innerCircleColor = innerCircleColor;
        setInnerColor = true;
        invalidate();
    }

    public int getExternalCircleColor() {
        if (setExternalColor)
            return externalCircleColor;
        return Color.WHITE;
    }

    /**
     * 开启外层圆环
     * @param externalCircleColor 颜色
     */
    public void openExternalCircle(int externalCircleColor) {
        this.externalCircleColor = externalCircleColor;
        setExternalColor = true;
        this.drawExternalCircle = true;
        invalidate();
    }

    /**
     *  获得控件左上角的坐标
     * @return
     */
    public Point getLeftTopPoint() {
        return leftTopPoint;
    }

    /**
     * 获取单位像素
     * @param unit 单位
     * @param value 单位数值
     * @return
     */
    private float getRawSize(int unit, float value) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(unit, value, metrics);
    }
}