package com.emre.space;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.graphics.drawable.*;
import android.os.*;
import android.view.*;
import com.emre.space.objects.*;
import java.util.*;

public class SpaceThread extends Thread {
    public static final float G = 6.64E-11f;
    public static final int MaxSatellites = 10000;
    Paint backGrounfPaint;
    private boolean disableCollision = false;
    private int idSatelliteSuivi = 0;
    Drawable imgAstro1;
    Drawable imgPlanete;
  	Drawable imgPlaneter;
	Drawable imgBlackHole;
    private boolean inverseNewPLanteGrav = false;
    Paint ligneNewPaint;
    private Bitmap mBackgroundImage;
    private int mCanvasHeight;
    private int mCanvasWidth;
    Context mContext;
    Handler mHandler;
    long mLastTime;
    boolean mRun = false;
    SurfaceHolder mSurfaceHolder;
    Paint nbrPlanPaint;
    int nbrPlanetes = 4;
    Paint pEntoureSiZomm;
    Planete[] planetes = new Planete[MaxSatellites];
    private Point pointDebutNew;
    private Point pointFinNew;
    private Satellite satAjouter = null;
    Paint satellitePaint;
    List<Satellite> satellites = new ArrayList();
    private double scale = 0.0d;
	float posX;
	float rayon;
	float posY;
	
	private PreferencesManager pM;

    public SpaceThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
        this.mSurfaceHolder = surfaceHolder;
        this.mContext = context;
        this.mHandler = handler;
        Resources res = context.getResources();
		pM = new PreferencesManager(context);
        this.imgPlanete = res.getDrawable(R.drawable.planete2_);
        this.imgPlaneter = res.getDrawable(R.drawable.planete2r);
        this.imgAstro1 = res.getDrawable(R.drawable.aste1);
		this.imgBlackHole = res.getDrawable(R.drawable.blackhole);
        this.backGrounfPaint = new Paint();
        this.backGrounfPaint.setARGB(255, 0, 0, 0);
        this.satellitePaint = new Paint();
        this.satellitePaint.setARGB(255, 255, 255, 200);
        this.satellitePaint.setAntiAlias(true);
        this.ligneNewPaint = new Paint();
        this.ligneNewPaint.setARGB(255, 255, 255, 255);
		ligneNewPaint.setColor(Color.parseColor("#18c4db"));
        this.ligneNewPaint.setAntiAlias(false);
        this.nbrPlanPaint = new Paint();
        this.nbrPlanPaint.setAntiAlias(false);
        this.nbrPlanPaint.setARGB(255, 255, 255, 255);
		nbrPlanPaint.setColor(Color.parseColor("#18c4db"));
        this.mLastTime = System.currentTimeMillis();
        this.mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.space);
	}

    public double changeZom(float deltaDist) {
        double d;
        synchronized (this.mSurfaceHolder) {
            this.scale = (double) deltaDist;
            if (this.scale >= 0.91d) {
                this.scale = 0.9d;
            }
            if (this.scale < -6.0d) {
                this.scale = -6.0d;
            }
            d = this.scale;
        }
        return d;
    }

    public void addPlanete_(float x, float y) {
        synchronized (this.mSurfaceHolder) {
            double cameraX = 0.0d;
            double cameraY = 0.0d;
            if (idSatelliteSuivi != 0) {
                cameraX = ((double) (this.mCanvasWidth / 2)) - ((Satellite) this.satellites.get(this.idSatelliteSuivi - 1)).positionX;
                cameraY = ((double) (this.mCanvasHeight / 2)) - ((Satellite) this.satellites.get(this.idSatelliteSuivi - 1)).positionY;
            }
            float posX = (float) ((((double) ((float) (((double) x) - cameraX))) - ((this.scale * ((double) this.mCanvasWidth)) / 2.0d)) / (1.0d - this.scale));
            float posY = (float) ((((double) ((float) (((double) y) - cameraY))) - ((this.scale * ((double) this.mCanvasHeight)) / 2.0d)) / (1.0d - this.scale));
            Planete p = new Planete();
			
			if(pM.getPref(pM.BLACK_HOLE)){
				p.masse = 20000000;
			}

			if(pM.getPref(pM.NM)){
				p.masse = -150000;
			}else if (!pM.getPref(pM.NM)){
            	p.masse = 150000;
			}

            p.densite = 10;
            p.rayon = 12;
            p.position.x = (int) posX;
            p.position.y = (int) posY;
            Planete[] planeteArr = this.planetes;
            int i = this.nbrPlanetes;
            nbrPlanetes = i + 1;
            planeteArr[i] = p;
        }
    }
	
	Point point1;
	Point point2;

    public void run() {
	
        while (this.mRun) {
			
            Canvas c = null;
            try {
                c = this.mSurfaceHolder.lockCanvas(null);
                synchronized (this.mSurfaceHolder) {
                    doPhysics();
                    doDraw(c);
                }
                if (c != null) {
                    this.mSurfaceHolder.unlockCanvasAndPost(c);
                }
            } catch (Exception e) {
                if (c != null) {
                    this.mSurfaceHolder.unlockCanvasAndPost(c);
                }
            } catch (Throwable th) {
                if (c != null) {
                    this.mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
			

			Random generator1 = new Random(); 
			int tryi = generator1.nextInt(10) + 1;
			if(tryi==5){
			Random generator = new Random(); 
			int rN = generator.nextInt(20) + 1;
			int rN1= generator.nextInt(10) + 1;
			int abcd= generator.nextInt(3) + 1;
			this.point1 = new Point();
			this.point1.set((int) mCanvasWidth*rN*3 / 2+rN*10, (int) mCanvasHeight /2+rN*20);
			this.point2 = new Point();
			this.point2.set((int) mCanvasWidth*rN1 / 2+rN*10, (int) mCanvasHeight /2+rN*20);
			
			addAsteroid(point1,point2,abcd);
			
			}
        }
    }

    public void init() {
        synchronized (this.mSurfaceHolder) {
            this.nbrPlanetes = 0;
            this.satellites.clear();
            Satellite sat = new Satellite();
            sat.masse = 350;
            sat.densite = 10;
            sat.rayon = 2;
            sat.positionX = (double) (this.mCanvasWidth / 2);
            sat.positionY = (double) ((this.mCanvasHeight / 2) - 100);
            sat.velociteX = 0.0d;
            sat.velociteY = 0.0d;
            this.satellites.add(sat);
            this.idSatelliteSuivi = 0;
            this.pEntoureSiZomm = new Paint();
            this.pEntoureSiZomm.setColor(Color.parseColor("#18c4db"));
            this.pEntoureSiZomm.setStyle(Style.FILL_AND_STROKE);
            this.pEntoureSiZomm.setAntiAlias(true);
        }
    }

	/*
	public void addP(){
		Planete unePlan = new Planete();
		Random generator = new Random(); 
		int rN = generator.nextInt(20) + 1;
		unePlan.position.x = this.mCanvasWidth / 2+rN*20;
		unePlan.position.y = this.mCanvasHeight / 2+rN*20;
		
		if(pM.getPref(pM.BLACK_HOLE)){
			unePlan.masse = 20000000;
		}

		if(pM.getPref(pM.NM)){
			unePlan.masse = -150000;
		}else if (pM.getPref(pM.NM)){
			unePlan.masse = 150000;
		}
		
		unePlan.densite = 10;
		unePlan.rayon = 13;
		//	unePlan.velocite = 0.03d;
		Planete[] planeteArr = this.planetes;
		int i = this.nbrPlanetes;
		this.nbrPlanetes = i + 1;
		planeteArr[i] = unePlan;
	}
	*/

    public void doDraw(Canvas canvas) {
        double cameraX = 0.0d;
        double cameraY = 0.0d;
        if (this.idSatelliteSuivi > this.satellites.size() || this.idSatelliteSuivi < 0) {
            this.idSatelliteSuivi = 0;
        }
        if (this.idSatelliteSuivi != 0) {
            cameraX = ((double) (this.mCanvasWidth / 2)) - ((Satellite) this.satellites.get(this.idSatelliteSuivi - 1)).positionX;
            cameraY = ((double) (this.mCanvasHeight / 2)) - ((Satellite) this.satellites.get(this.idSatelliteSuivi - 1)).positionY;
        }
        canvas.drawBitmap(this.mBackgroundImage, 0.0f, 0.0f, null);
        for (int z = 0; z < this.nbrPlanetes; z++) {
            Planete planete = this.planetes[z];
            float posX = (float) (((double) planete.position.x) + cameraX);
            float posY = (float) (((double) planete.position.y) + cameraY);
            posX = (float) (((double) posX) + (((double) (((float) (this.mCanvasWidth / 2)) - posX)) * this.scale));
            posY = (float) (((double) posY) + (((double) (((float) (this.mCanvasHeight / 2)) - posY)) * this.scale));
            float rayon = (float) Math.abs(((double) planete.rayon) * (3.0d - this.scale));

            if (planete.masse > 0) {
                if (planete.masse>20000000){
                    imgBlackHole.setBounds((int) (posX - rayon), (int) (posY - rayon), (int) (rayon + posX), (int) (rayon + posY));
                    imgBlackHole.draw(canvas);
                } else {
                    imgPlanete.setBounds((int) (posX - rayon), (int) (posY - rayon), (int) (rayon + posX), (int) (rayon + posY));
                    imgPlanete.draw(canvas);
                }
            } else {
                imgPlaneter.setBounds((int) (posX - rayon), (int) (posY - rayon), (int) (rayon + posX), (int) (rayon + posY));
                imgPlaneter.draw(canvas);
            }
			
            if (this.scale >= 0.7d) {
             //   Rect rect = new Rect((int) ((posX - rayon) - 5.0f), (int) ((posY - rayon) - 5.0f), (int) ((5.0f + rayon) + posX), (int) ((5.0f + rayon) + posY));
                canvas.drawCircle(posX, posY, 13.0f, pEntoureSiZomm);
            }
        }

        for (Satellite s : this.satellites) {
            this.satellitePaint.setARGB(255, s.colR, s.colG, s.colB);
            posX = (float) (s.positionX + cameraX);
            posY = (float) (s.positionY + cameraY);
            posX = (float) (((double) posX) + (((double) (((float) (this.mCanvasWidth / 2)) - posX)) * this.scale));
            posY = (float) (((double) posY) + (((double) (((float) (this.mCanvasHeight / 2)) - posY)) * this.scale));
            rayon = (float) Math.abs(((double) ((float) s.rayon)) * (4.0d - this.scale));
            this.imgAstro1.setBounds((int) (posX - rayon), (int) (posY - rayon), (int) (posX + rayon), (int) (posY + rayon));
            this.imgAstro1.draw(canvas);
            for (Point unpoint : s.anciennes) {
                posX = (float) (((double) unpoint.x) + cameraX);
                posY = (float) (((double) unpoint.y) + cameraY);
                canvas.drawPoint((float) (((double) posX) + (((double) (((float) (this.mCanvasWidth / 2)) - posX)) * this.scale)), (float) (((double) posY) + (((double) (((float) (this.mCanvasHeight / 2)) - posY)) * this.scale)), this.satellitePaint);
            }
        }
        if (this.pointDebutNew != null) {
            canvas.drawLine((float) this.pointDebutNew.x, (float) this.pointDebutNew.y, (float) this.pointFinNew.x, (float) this.pointFinNew.y, ligneNewPaint);
        }
		
        canvas.drawText("Number of Sat:" + this.satellites.size(), 15.0f, (float) (this.mCanvasHeight - 10), this.nbrPlanPaint);
        canvas.drawText("Zoom:" + ((int) (this.scale * 10.0d)), 15.0f, (float) (this.mCanvasHeight - 30), this.nbrPlanPaint);
    }

    public void doPhysics() {
        if (this.satAjouter != null) {
            this.satellites.add(this.satAjouter);
            this.satAjouter = null;
        }
        long now = System.currentTimeMillis();
        if (this.mLastTime <= now) {
            int i;
            Satellite s;
            double distX;
            double distY;
            double distanceSquared;
            double distance;
            double normalDistY;
            double force;
            double elapsed = (double) (now - this.mLastTime);
            int nbrSats = this.satellites.size();
            for (int k = 0; k < this.nbrPlanetes; k++) {
                Planete p = this.planetes[k];
                for (i = 0; i < nbrSats; i++) {
                    s = (Satellite) this.satellites.get(i);
                    distX = s.positionX - ((double) p.position.x);
                    distY = s.positionY - ((double) p.position.y);
                    distanceSquared = (distX * distX) + (distY * distY);
                    distance = Math.sqrt(distanceSquared);
                    if (1.0d + distance > ((double) (p.rayon + s.rayon))) {
                        normalDistY = distY / distance;
                        force = ((double) ((G * ((float) p.masse)) * ((float) s.masse))) / distanceSquared;
                        s.forceX -= force * (distX / distance);
                        s.forceY -= force * normalDistY;
                    } else if (!this.disableCollision) {
                        if (s.masse > 10000) {
                            this.nbrPlanetes--;
                        }
                      
                        if (i + 1 == this.idSatelliteSuivi) {
                            this.idSatelliteSuivi = 0;
                        }
                        if (i + 1 < this.idSatelliteSuivi) {
                            this.idSatelliteSuivi--;
                        }
                        this.satellites.remove(i);
                        nbrSats--;
                    } else if (distance > 4.0d) {
                        normalDistY = distY / distance;
                        force = ((double) ((G * ((float) p.masse)) * ((float) s.masse))) / distanceSquared;
                        s.forceX -= force * (distX / distance);
                        s.forceY -= force * normalDistY;
                    }
                    if (s.positionX < -1000.0d || s.positionX > ((double) (this.mCanvasWidth + 1000)) || s.positionY < -1000.0d || s.positionY > ((double) (this.mCanvasHeight + 1000))) {
                        this.satellites.remove(i);
                        if (i + 1 < this.idSatelliteSuivi) {
                            this.idSatelliteSuivi--;
                        }
                        nbrSats--;
                    }
                }
            }
            int nbrsat = this.satellites.size();
            for (i = 0; i < nbrsat; i++) {
                int c = i;
                while (c < nbrsat - 1) {
                    Satellite s1 = (Satellite) this.satellites.get(i);
                    Satellite s2 = (Satellite) this.satellites.get(c + 1);
                    distX = s1.positionX - s2.positionX;
                    distY = s1.positionY - s2.positionY;
                    distanceSquared = (distX * distX) + (distY * distY);
                    distance = Math.sqrt(distanceSquared);
                    if (Math.round(distance) >= (s1.rayon + s2.rayon) + 3) {
                        double normalDistX = distX / distance;
                        normalDistY = distY / distance;
                        force = ((double) ((G * ((float) s1.masse)) * ((float) s2.masse))) / distanceSquared;
                        s1.forceX -= force * normalDistX;
                        s1.forceY -= force * normalDistY;
                        s2.forceX += force * normalDistX;
                        s2.forceY += force * normalDistY;
                    } else if (!this.disableCollision) {
                        Satellite sGardee;
                        Satellite sDetruit;
                        if (s1.masse >= s2.masse) {
                            sGardee = s1;
                            sDetruit = s2;
                        } else {
                            sGardee = s2;
                            sDetruit = s1;
                        }
                        sGardee.velociteY = ((sGardee.velociteY * ((double) sGardee.masse)) + (sDetruit.velociteY * ((double) sDetruit.masse))) / ((double) (sGardee.masse + sDetruit.masse));
                        sGardee.velociteX = ((sGardee.velociteX * ((double) sGardee.masse)) + (sDetruit.velociteX * ((double) sDetruit.masse))) / ((double) (sGardee.masse + sDetruit.masse));
                        sGardee.masse += sDetruit.masse / 2;
                        sGardee.rayon += sDetruit.rayon / 2;
                     
                        if (i + 1 == this.idSatelliteSuivi || c + 2 == this.idSatelliteSuivi) {
                            if (sGardee == s1) {
                                this.idSatelliteSuivi = i + 1;
                            } else {
                                this.idSatelliteSuivi = c + 2;
                            }
                        } else if (this.idSatelliteSuivi > c + 2) {
                            this.idSatelliteSuivi--;
                        }
                        nbrsat--;
                        this.satellites.remove(sDetruit);
                    }
                    c++;
                }
            }
            for (Satellite s3 : this.satellites) {
                s3.accelerationX = s3.forceX * ((double) s3.masse);
                s3.accelerationY = s3.forceY * ((double) s3.masse);
                s3.forceX = 0.0d;
                s3.forceY = 0.0d;
                if (elapsed > 100.0d) {
                    elapsed = 100.0d;
                }
                s3.velociteX += s3.accelerationX * (elapsed / 2.0d);
                s3.velociteY += s3.accelerationY * (elapsed / 2.0d);
                s3.positionX += s3.velociteX * elapsed;
                s3.positionY += s3.velociteY * elapsed;
                s3.setNewPos(new Point((int) Math.round(s3.positionX), (int) Math.round(s3.positionY)));
            }
            this.mLastTime = now;
        }
    }

    public void setDoigtLeve(Point origine, Point fin) {
        synchronized (this.mSurfaceHolder) {
            if (this.idSatelliteSuivi > this.satellites.size() || this.idSatelliteSuivi < 0) {
                this.idSatelliteSuivi = 0;
            }
            this.pointDebutNew = null;
            this.pointFinNew = null;
            double cameraX = 0.0d;
            double cameraY = 0.0d;
            if (this.idSatelliteSuivi != 0) {
                cameraX = ((double) (this.mCanvasWidth / 2)) - ((Satellite) this.satellites.get(this.idSatelliteSuivi - 1)).positionX;
                cameraY = ((double) (this.mCanvasHeight / 2)) - ((Satellite) this.satellites.get(this.idSatelliteSuivi - 1)).positionY;
            }
            int deltaX = fin.x - origine.x;
            int deltaY = fin.y - origine.y;
            Satellite s = new Satellite();
            s.masse = 3500;
            s.rayon = 2;
            s.densite = 100;
            float posY = (float) ((((double) ((float) (((double) origine.y) - cameraY))) - ((this.scale * ((double) this.mCanvasHeight)) / 2.0d)) / (1.0d - this.scale));
            s.positionX = (double) ((float) ((((double) ((float) (((double) origine.x) - cameraX))) - ((this.scale * ((double) this.mCanvasWidth)) / 2.0d)) / (1.0d - this.scale)));
            s.positionY = (double) posY;
            s.velociteX = ((double) deltaX) * 4.0E-4d;
            s.velociteY = ((double) deltaY) * 4.0E-4d;
            this.satAjouter = s;
        }
    }

	public void addAsteroid(Point origine, Point fin, int random) {
        synchronized (this.mSurfaceHolder) {
            if (this.idSatelliteSuivi > this.satellites.size() || this.idSatelliteSuivi < 0) {
                this.idSatelliteSuivi = 0;
            }
            this.pointDebutNew = null;
            this.pointFinNew = null;
            double cameraX = 0.0d;
            double cameraY = 0.0d;
            if (this.idSatelliteSuivi != 0) {
                cameraX = ((double) (this.mCanvasWidth / 2)) - ((Satellite) this.satellites.get(this.idSatelliteSuivi - 1)).positionX;
                cameraY = ((double) (this.mCanvasHeight / 2)) - ((Satellite) this.satellites.get(this.idSatelliteSuivi - 1)).positionY;
            }
            int deltaX = fin.x - origine.x;
            int deltaY = fin.y - origine.y;
            Satellite s = new Satellite();
            s.masse = 3500;
            s.rayon = random;
            s.densite = 100;
            float posY = (float) ((((double) ((float) (((double) origine.y) - cameraY))) - ((this.scale * ((double) this.mCanvasHeight)) / 2.0d)) / (1.0d - this.scale));
            s.positionX = (double) ((float) ((((double) ((float) (((double) origine.x) - cameraX))) - ((this.scale * ((double) this.mCanvasWidth)) / 2.0d)) / (1.0d - this.scale)));
            s.positionY = (double) posY;
            s.velociteX = ((double) deltaX) * 4.0E-4d;
            s.velociteY = ((double) deltaY) * 4.0E-4d;
            this.satAjouter = s;
        }
    }
	
	
    public void setCanvasSize(int width, int height) {
        this.mCanvasHeight = height;
        this.mCanvasWidth = width;
        init();
    }

    public void setRunning(boolean b) {
        this.mRun = b;
    }

    public void ChangerEchelle(boolean plus) {
        if (plus) {
            this.scale += 0.1d;
        } else {
            this.scale -= 0.1d;
        }
        if (this.scale >= 1.0d) {
            this.scale = 0.9d;
        }
        if (this.scale < -4.0d) {
            this.scale = -4.0d;
        }
    }
    public void setDoigtBouge(Point origine, Point fin) {}

    public void changeSatSuivant() {
        this.idSatelliteSuivi++;
    }

    public void resetView() {
        this.idSatelliteSuivi = 0;
    }

    public void changeSatPrec() {
        this.idSatelliteSuivi--;
    }
}
