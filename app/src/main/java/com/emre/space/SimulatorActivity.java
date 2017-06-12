package com.emre.space;

import android.app.*;
import android.os.*;
import android.widget.*;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class SimulatorActivity extends Activity {

    static String ID = "ca-app-pub-5942424100141990/2430621067";

    private InterstitialAd mInterstitialAd;
    static final int MENU_NEW_GAME = 1;
    private SpaceView spaceView;
	private Switch nMS , sBH;
	private PreferencesManager p;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(MENU_NEW_GAME);
        if (savedInstanceState != null) {
            this.spaceView.restoreState(savedInstanceState);
        }
        setContentView(R.layout.main);
        this.spaceView = (SpaceView) findViewById(R.id.Game);
		p = new PreferencesManager(this);
		
		nMS = (Switch) findViewById(R.id.nMS);
		
		sBH= (Switch) findViewById(R.id.blackHoleSwitch);
		
		
		try {
		nMS.setChecked(p.getPref(p.NM));
		sBH.setChecked(p.getPref(p.BLACK_HOLE));
		}catch (Exception e) {
			
		}
		
		nMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
					p.setPref(p.NM, b);
					if(sBH.isChecked() && b){
						sBH.setChecked(false);
					}
				}
			});
			
			
		sBH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
					p.setPref(p.BLACK_HOLE, b);
					if(nMS.isChecked() && b){
						nMS.setChecked(false);
					}
				}
			});
		
			
		/*
        ((Button) findViewById(R.id.Button01)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                spacesim.this.spaceView.spaceThread.changeSatPrec();
            }
        });
        ((Button) findViewById(R.id.Button02)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                spacesim.this.spaceView.spaceThread.changeSatSuivant();
            }
        });
		*/

        this.mInterstitialAd = new InterstitialAd(this);
        this.mInterstitialAd.setAdUnitId(ID);
        this.mInterstitialAd.setAdListener(new AdListener() {
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("4b5d467c88b7bd63").addTestDevice("04157df47a383a0c").build());
            }
        });
        showAdWhenLoaded(0);
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("4b5d467c88b7bd63").addTestDevice("04157df47a383a0c").build());

    }

    private void showAdWhenLoaded(int extraDelay) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    showAdWhenLoaded(0);
                }
            }
        }, (long) (extraDelay + 400));
    }

    public void terminate() {
        super.onDestroy();
        finish();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onRestart() {
        super.onRestart();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

    public void quit() {
        terminate();
    }
}
