package com.emre.space.objects;

import android.graphics.Point;

public class Satellite {
    private static final int maxTrainee = 60;
    public double accelerationX;
    public double accelerationY;
    public Point[] anciennes;
    public int colB;
    public int colG;
    public int colR;
    public long densite;
    public double forceX;
    public double forceY;
    public long masse;
    private int offsetPos;
    public double positionX;
    public double positionY;
    public long rayon;
    public double velociteX;
    public double velociteY;

    public Satellite(long mMasse, double mVelociteX, double mVelociteY, double mPositionX, double mPositionY) {
        this();
        this.masse = mMasse;
        this.velociteX = mVelociteX;
        this.velociteY = mVelociteY;
        this.positionX = mPositionX;
        this.positionY = mPositionY;
        this.rayon = 2;
        this.densite = 10;
    }

    public Satellite() {
        this.anciennes = new Point[maxTrainee];
        this.offsetPos = 0;
        this.colR = (int) ((Math.random() * 60.0d) + 195.0d);
        this.colG = (int) ((Math.random() * 60.0d) + 195.0d);
        this.colB = (int) ((Math.random() * 60.0d) + 195.0d);
        for (int i = 0; i < maxTrainee; i++) {
            this.anciennes[i] = new Point(-10, -10);
        }
    }

    public void setNewPos(Point p) {
        this.offsetPos++;
        if (this.offsetPos > 59) {
            this.offsetPos = 0;
        }
        this.anciennes[this.offsetPos] = p;
    }
}
