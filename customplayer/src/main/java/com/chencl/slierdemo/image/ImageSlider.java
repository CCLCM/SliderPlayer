package com.chencl.slierdemo.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.chencl.slierdemo.R;

import java.util.ArrayList;


public class ImageSlider {
    private static int START_INDEX = 1000;
    private ViewPager slider;
    private ArrayList<String> mimageViewList;
    private static int HAND_MESSAGE_TAG = 1;
    int DELAY = 3000;//切换的延迟时间为2400毫秒
    boolean isAuto = true;//是否值自动轮播，默认为true
    boolean isFromUser = false;//用来标志用户是否滑动屏幕
    private static int cursorpoint = 0;

    private Activity activity;

    public ImageSlider(Activity activity, ArrayList<String> imageViews) {
        this.activity = activity;
        this.mimageViewList = imageViews;

    }


    public View initView() {

        if (mimageViewList == null || mimageViewList.size() == 0) {
            View inflate = View.inflate(activity, R.layout.activity_nothing, null);
            return inflate;
        }


        View view = View.inflate(activity, R.layout.slider_item, null);
        slider = (ViewPager) view.findViewById(R.id.slider);

        slider.setAdapter(new sliderAdapter());
        slider.setCurrentItem(START_INDEX);
        cursorpoint = START_INDEX;

        //执行定时任务
        handler.sendEmptyMessageDelayed(HAND_MESSAGE_TAG, DELAY);
        slider.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                cursorpoint = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        //手指离开ViewPager的时候调用，发送延迟消息，自动轮播

                        isAuto = true;
                        if (isFromUser) {
                            isFromUser = false;
                            handler.sendEmptyMessageDelayed(HAND_MESSAGE_TAG, DELAY);
                        }
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        //手指拖动ViewPager进行手动切换的时候，停止自动轮播

                        isAuto = false;
                        handler.removeMessages(HAND_MESSAGE_TAG);
                        isFromUser = true;
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }


    //定义一个handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (isAuto) {

                    slider.setCurrentItem(cursorpoint, true);
                    cursorpoint++;
                    handler.sendEmptyMessageDelayed(HAND_MESSAGE_TAG, DELAY);

                }
            }
        }
    };


    class sliderAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = LinearLayout.inflate(activity, R.layout.image_item, null);
            ImageView image_it = view.findViewById(R.id.image_it);
            //测试发现 轮播的时候图片有卡顿, 原因是图片太大, 讲图片压缩为原来的 1/2像素,
            // 肉眼基本看不出来像素差别, 轮播变为流畅
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize=2;//直接设置它的压缩率，表示1/2

            Bitmap bitmap = BitmapFactory.decodeFile(mimageViewList.get(position % mimageViewList.size()),options);

            image_it.setImageBitmap(bitmap);
            image_it.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
