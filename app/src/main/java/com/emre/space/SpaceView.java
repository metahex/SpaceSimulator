package com.emre.space;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class SpaceView extends SurfaceView implements Callback {
    Long downTime;
    boolean dualDoigt = false;
    boolean firstDouble = true;
    double lastScaled = 0.0d;
    Context mContext;
    SurfaceHolder mSurfaceHolder;
   // int nbrDoigtsTouche = 0;
    float oldDist = 0.0f;
    double originalScale = 0.0d;
    Point positionDebut;
    Point positionFin;
	//Point random;
    SpaceThread spaceThread;

    public SpaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        this.mSurfaceHolder = holder;
        holder.addCallback(this);
        setFocusable(true);
        this.mContext = context;
        this.spaceThread = new SpaceThread(holder, context, new Handler());
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.spaceThread.setCanvasSize(width, height);
        setFocusable(true);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.spaceThread = new SpaceThread(holder, this.mContext, new Handler());
        this.spaceThread.setRunning(true);
        this.spaceThread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        this.spaceThread.setRunning(false);
        while (retry) {
            try {
                this.spaceThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case 19:
                this.spaceThread.ChangerEchelle(false);
                return true;
            case 20:
                this.spaceThread.ChangerEchelle(true);
                return true;
            case 21:
                this.spaceThread.changeSatPrec();
                return true;
            case 22:
                this.spaceThread.changeSatSuivant();
                return true;
            case 23:
                this.spaceThread.resetView();
                return true;
            default:
                return false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        double deltaY;
        switch (event.getAction()) {
            case 0:
                this.downTime = Long.valueOf(System.currentTimeMillis());
                this.positionDebut = new Point();
                this.positionDebut.set((int) event.getX(), (int) event.getY());
                this.positionFin = new Point();
                this.positionFin.set((int) event.getX(), (int) event.getY());
                break;
            case 1:
                if (!this.dualDoigt) {
                    double deltaX = (double) Math.abs(event.getX() - ((float) this.positionFin.x));
                    deltaY = (double) Math.abs(event.getY() - ((float) this.positionFin.y));
                    double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
                    if (System.currentTimeMillis() - this.downTime.longValue() > 700 && distance < 5.0d) {
                        this.spaceThread.addplanet_(event.getX(), event.getY());
                        break;
                    }
                   spaceThread.createAsteroid(this.positionDebut, this.positionFin);
                   break;
                }
                this.originalScale = this.lastScaled;
                this.dualDoigt = false;
                this.firstDouble = true;
                break;
                
            case 2:
                if (event.getPointerCount() > 1) {
                    if (this.firstDouble) {
                        this.oldDist = spacing(event);
                        this.firstDouble = false;
                    }
                    float deltaDist = this.oldDist - spacing(event);
                    this.dualDoigt = true;
                    this.lastScaled = this.spaceThread.changeZom((float) (this.originalScale + ((double) (deltaDist / 350.0f))));
                }
                deltaY = (double) Math.abs(event.getY() - ((float) this.positionFin.y));
                if (((double) Math.abs(event.getX() - ((float) this.positionFin.x))) > 5.0d || deltaY > 5.0d) {
                    this.positionFin.set((int) event.getX(), (int) event.getY());
                    this.spaceThread.setDoigtBouge(this.positionDebut, this.positionFin);
                    break;
                }
        }
        super.onTouchEvent(event);
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt((x * x) + (y * y));
    }

    public Bundle saveState(Bundle map) {
        synchronized (this.mSurfaceHolder) {
        }
        return map;
    }

    public synchronized void restoreState(Bundle savedState) {
        synchronized (this.mSurfaceHolder) {
        }
    }
}
