package com.zh.headwalll.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.zh.headwalll.bean.HeaderBean;


@SuppressLint("AppCompatCustomView")
public class HeadWallView extends ImageView {


    private Bitmap mBitmap;

    private final Paint mCirclePaint = new Paint();
    private Canvas mCanvas;

    private Thread drawThread = null;

    public HeadWallView(Context context) {
        super(context);
        init();
    }

    public HeadWallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeadWallView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     *
     */
    private void init() {
        mBitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setAntiAlias(true);
        this.setScaleType(ScaleType.MATRIX);
        drawThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Activity ctx = (Activity) HeadWallView.this.getContext();

                while (!Thread.currentThread().isInterrupted()) {
                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mBitmap == null) {
                                return;
                            }
                            HeadWallView.this.setImageBitmap(mBitmap);
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        drawThread.start();
    }


    public void setHeader(HeaderBean bean) {
        if (bean == null) {
            return;
        }
        int w = bean.getBitmap().getWidth();
        int h = bean.getBitmap().getHeight();
        mCanvas.drawBitmap(bean.getBitmap(), new Rect(0, 0, w, h), new Rect(bean.getX(), bean.getY(), bean.getX() + w, bean.getY() + h), mCirclePaint);
        this.setImageBitmap(mBitmap);
    }


    /**
     * 创建圆形位图
     *
     * @param source   原图片位图
     * @param diameter 圆形位图的直径
     * @return
     */
    private Bitmap createCircleImage(Bitmap source, int diameter) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap clipBitmap;
        if (width > height) {
            int x = (width - height) / 2;
            int y = 0;
            clipBitmap = Bitmap.createBitmap(source, x, y, height, height);
        } else if (width < height) {
            int x = 0;
            int y = (height - width) / 2;
            clipBitmap = Bitmap.createBitmap(source, x, y, width, width);
        } else {
            clipBitmap = source;
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(clipBitmap, diameter, diameter, true);
        Bitmap outputBitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, 0, 0, paint);
//        source.recycle();
//        clipBitmap.recycle();
//        scaledBitmap.recycle();
        return outputBitmap;
    }
}