package com.zh.headwalll;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zh.headwalll.bean.HeaderBean;
import com.zh.headwalll.utils.DensityUtil;
import com.zh.headwalll.view.HeadWallView;

import java.util.ArrayList;
import java.util.Random;


public class TestActivity extends Activity {
    private RelativeLayout content;
    private Random random = new Random();
    private static final String TAG = "TestActivity";
    private Context mContext;
    private RequestOptions options;
    private HeadWallView sv_header;
    private HeadWallView sv_header2;


    private int screenHeigth;
    private int screenWidth;
    private int viewLengh;


    private Handler mHander = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            addHeader();
            sendEmptyMessageDelayed(1, 1800);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mContext = this;
        content = findViewById(R.id.rl_root);
        sv_header = findViewById(R.id.sv_headers);
        sv_header2 = findViewById(R.id.sv_headers2);
        testData();


        screenWidth = DensityUtil.getScreenWidth(mContext);
        screenHeigth = DensityUtil.getScreenHeight(mContext);
        viewLengh = DensityUtil.dip2px(mContext, 58); //飞出的view大小

        options = new RequestOptions()
                .centerCrop()
                .circleCrop() //圆角
                .placeholder(R.drawable.ic_person_head)
                .error(android.R.drawable.stat_notify_error)
                .priority(Priority.LOW)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        mHander.sendEmptyMessageDelayed(1, 1000);
    }

    private void addHeader() {
        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String url = headers.get(random.nextInt(headers.size()));
            urls.add(url);
        }
        makeBeans(urls);
    }

    private ArrayList<String> headers = new ArrayList<>();

    private void testData() {
        headers.add("https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJ8nXLPVGjIwSnteNbB0frW4KgK6JzoZ8icicZsqL8A25xRvEhyT2u0MNMnNc40rKJU1AAPibk4RJq6A/132");
        headers.add("https://wx.qlogo.cn/mmopen/vi_32/IzicuKxaqXgG2ZG1WrialgltkEMZYojhBMkaHn8Xk5wicnAq5AGeb0OXCqia5WZSPfQyCb64RAMcq69BCAIFwQybCA/132");
        headers.add("https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJF4gWZwBVQiahY7I7L3ZA65zclKhvY7CXibyibrfHapsQibSuonhWRAHbvZiaruleEiaaPU0PNz0BUcZTw/132");
    }

    private void makeBeans(ArrayList<String> urls) {
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            final int finalI = i;
            Glide.with(mContext).asBitmap().load(url).apply(options).into(new SimpleTarget<Bitmap>() {

                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    int x = 1 + (int) (Math.random() * (screenWidth - viewLengh));
                    int y = 1 + (int) (Math.random() * (DensityUtil.dip2px(mContext, 200) - viewLengh)); //200是布局里view的高度
                    HeaderBean headerBean = new HeaderBean();
                    headerBean.setBitmap(resource);
                    headerBean.setX(x);
                    headerBean.setY(y);
                    if ((finalI + 1) % 2 == 1) { //一边view飞一个
                        int[] xy = new int[]{x, y};//相对view的坐标
                        int[] newXY = getDescendantCoordRelativeToSelf(sv_header, xy); //计算绝对坐标
                        startAnimation(TestActivity.this, resource, sv_header, headerBean, newXY[0] - (screenWidth / 2) + (viewLengh / 2), newXY[1] - screenHeigth + viewLengh);
                    } else {
                        int[] xy = new int[]{x, y};
                        int[] newXY = getDescendantCoordRelativeToSelf(sv_header2, xy);
                        startAnimation(TestActivity.this, resource, sv_header2, headerBean, newXY[0] - (screenWidth / 2) + (viewLengh / 2), newXY[1] - screenHeigth + viewLengh);
                    }

                }
            });
        }
        Log.d(TAG, "anim done");
    }

    private synchronized void startAnimation(Context ctx, Bitmap res, final HeadWallView headerView, final HeaderBean headerBean, int toFx, int toFy) {
        Log.d(TAG, "move to " + toFx + ":" + toFy);
        final ImageView imageView = new ImageView(ctx);
        imageView.setImageBitmap(res);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(viewLengh, viewLengh);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(random.nextInt(2000) - 500, 0, 0, 0);

        //平移动画
        TranslateAnimation translate = new TranslateAnimation(random.nextInt(2000) - 1000, toFx, 300, toFy);
        translate.setDuration(random.nextInt(1500) + 500);
        translate.setRepeatCount(0);
        translate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                headerView.setHeader(headerBean);
                content.removeView(imageView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        content.addView(imageView, lp);
        imageView.startAnimation(translate);
    }


    public int[] getDescendantCoordRelativeToSelf(View descendant, int[] coord) {
        float scale = 1.0f;
        float[] pt = {coord[0], coord[1]};
        //坐标值进行当前窗口的矩阵映射，比如View进行了旋转之类，它的坐标系会发生改变。map之后，会把点转换为改变之前的坐标。
        descendant.getMatrix().mapPoints(pt);
        //转换为直接父窗口的坐标
        scale *= descendant.getScaleX();
        pt[0] += descendant.getLeft();
        pt[1] += descendant.getTop();
        ViewParent viewParent = descendant.getParent();
        //循环获得父窗口的父窗口，并且依次计算在每个父窗口中的坐标
        while (viewParent instanceof View && viewParent != this) {
            final View view = (View) viewParent;
            view.getMatrix().mapPoints(pt);
            scale *= view.getScaleX();//这个是计算X的缩放值。此处可oof以不管
            //转换为相当于可视区左上角的坐标，scrollX，scollY是去掉滚动的影响
            pt[0] += view.getLeft() - view.getScrollX();
            pt[1] += view.getTop() - view.getScrollY();
            viewParent = view.getParent();
        }
        coord[0] = (int) Math.round(pt[0]);
        coord[1] = (int) Math.round(pt[1]);
        return coord;
    }
}
