package io.doist.chromakey;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity implements View.OnTouchListener {
    private static final int[] COLORS = new int[]{0xffffffff, 0xff00ff00, 0xff0000ff, 0xffff0000};

    private static final String KEY_COLOR = "color";

    private View mContentView;

    private GestureDetector mGestureDetector;

    private int mColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mContentView = findViewById(android.R.id.content);
        mContentView.setClickable(true);
        mContentView.setLongClickable(true);
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private int mTapCount = 0;
            private long mFirstTapAt = 0;

            @Override
            public void onLongPress(MotionEvent e) {
                finish();
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    mContentView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideDecor();
                        }
                    }, 1000);
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                if (SystemClock.elapsedRealtime() - mFirstTapAt < 500) {
                    mTapCount++;
                } else {
                    mTapCount = 1;
                    mFirstTapAt = SystemClock.elapsedRealtime();
                }

                if (mTapCount >= 3) {
                    mTapCount = 0;
                    setNextColor();
                    return true;
                } else {
                    return false;
                }
            }
        });

        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.screenBrightness = 1f;
        getWindow().setAttributes(attributes);

        mColor = savedInstanceState != null ? savedInstanceState.getInt(KEY_COLOR) - 1 : 0;
        setNextColor();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_COLOR, mColor);
    }

    private void setNextColor() {
        mContentView.setBackgroundColor(COLORS[mColor++ % COLORS.length]);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            hideDecor();
        }
    }

    private void hideDecor() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                   | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                   | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                   | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                   | View.SYSTEM_UI_FLAG_FULLSCREEN
                                                   | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
