package com.emre.space;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class PreferencesManager {

    Context context;

    public PreferencesManager(Context context1){
        context = context1;
    }
	
	public String NM = "negative_mass";
	public String BLACK_HOLE = "black_hole";

    public boolean getPref(String prefName) {

        File file = new File("/data/data/com.emre.space/files/"+prefName);

        boolean abc = false;

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }

            br.close();
        } catch (Exception e) {

        }

        if (text.toString().equals("1")){
            abc = true;
        }

        return abc;
    }

    public void setPref(String prefName, boolean var){

        try {
            FileOutputStream fOut = context.openFileOutput(prefName, Context.MODE_PRIVATE);

            String prefBool = var ? "1" : "0";

            fOut.write(prefBool.getBytes());
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
			// Log.e("Hata kodu3", "File write failed: ");
        }
    }

	public void setString(String prefName, String value){
		try {
            FileOutputStream fOut = context.openFileOutput(prefName, Context.MODE_PRIVATE);

            fOut.write(String.valueOf(value).getBytes());
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            //Log.e("Hata kodu3", "File write failed: ");
        }
	}

	public String getString(String prefName){

		File file = new File("/data/data/com.emre.space/files/"+prefName);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
				text.append("\n");
            }

            br.close();
        } catch (Exception e) {

        }

        return text.toString();
	}
}
