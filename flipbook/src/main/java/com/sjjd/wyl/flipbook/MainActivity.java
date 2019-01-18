package com.sjjd.wyl.flipbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sjjd.wyl.flipbook.page.PageLoader;
import com.sjjd.wyl.flipbook.page.PageMode;
import com.sjjd.wyl.flipbook.page.PageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sjjd.wyl.flipbook.page.PageLoader.resizeBgImage;

public class MainActivity extends AppCompatActivity {

    PageView mPageView;
    private String RES_DIR = "pages";
    private String RES_PRE = "p";
    private PageLoader mPageLoader;
    private List<Bitmap> mCurPageList;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initLoader();
                    break;
            }
        }
    };

    private void initLoader() {
        mPageLoader = mPageView.getPageLoader(mCurPageList);
        mPageLoader.setPageMode(PageMode.SIMULATION);
        mPageLoader.setOnPageChangeListener(new PageLoader.OnPageChangeListener() {
            @Override
            public void onChapterChange(int pos) {

            }

            @Override
            public void requestChapters(List<Bitmap> requestChapters) {

            }

            @Override
            public void onCategoryFinish(List<Bitmap> chapters) {

            }

            @Override
            public void onPageCountChange(int count) {

            }

            @Override
            public void onPageChange(int pos) {

            }
        });

        mPageView.setTouchListener(new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                return true;
            }

            @Override
            public void center() {

            }

            @Override
            public void prePage() {
            }

            @Override
            public void nextPage() {
            }

            @Override
            public void cancel() {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPageView = findViewById(R.id.page);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mCurPageList = new ArrayList<>();
                    String[] pages = getAssets().list(RES_DIR);
                    for (int i = 1; i <= pages.length; i++) {
                        //Bitmap mBitmap = BitmapFactory.decodeResource(mPageView.getResources(), mPageView.getResources().getIdentifier("ebook" + i, "drawable", mPageView.getContext().getPackageName())).copy(Bitmap.Config.ARGB_8888, true);
                        Bitmap mBitmap = BitmapFactory.decodeStream(getAssets().open(RES_DIR + "/" + RES_PRE + i + ".jpg"));
                        mCurPageList.add(resizeBgImage(mBitmap, 1920, 1080));
                    }

                    mHandler.sendEmptyMessage(0);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPageLoader != null)
            mPageLoader.closePage();
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
