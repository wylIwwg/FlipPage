package com.sjjd.wyl.ebooks;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sjjd.wyl.ebooks.flip.FlipViewController;

public class FlipActivity extends AppCompatActivity {
    private FlipViewController flipView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flipView = new FlipViewController(this, FlipViewController.HORIZONTAL);

        flipView.setAdapter(new TravelAdapter(this));
        setContentView(flipView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flipView.onResume();
        hideBottomUIMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        flipView.onPause();
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
