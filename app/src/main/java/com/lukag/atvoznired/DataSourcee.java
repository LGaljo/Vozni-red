package com.lukag.atvoznired;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DataSourcee {
    private static HashMap<String, String> postaje = new HashMap<String, String>();
    public static String[] samoPostaje = null;

    /**
     * Metoda napolni HashMap in String Array s podatki o postajah
     * @param context - kontekst razreda iz katerega je klicana metoda
     */
    public static void init(Context context) {
        String job = postajeFromAsset(context);
        try {
            JSONArray arr = new JSONArray(job);
            samoPostaje = new String[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String p = o.getString("postaja");
                String in = o.getString("id");
                postaje.put(p, in);
                samoPostaje[i] = p;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Metoda, ki mi iz JSON datoteke prebere vse postaje
     *  Vrne mi string v katerem je postaja
     */
    public static String postajeFromAsset(Context con) {
        String v_json = null;

        try {
            InputStream is = con.getAssets().open("Postaje.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            v_json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return v_json;
    }

    /**
     * Metoda iz sharedPreferenca dobi postaji in datum ter nastavi vrednosti v prave objekte
     * @param context - kontekst razreda iz katerega je klicana metoda
     * @param vp - AutoCompleteTextView za vstopno postajo
     * @param ip - AutoCompleteTextView za izstopo postajo
     * @param dateView - TextView za tekst koledarja
     */
    public static void nastaviZadnjiIskani(Context context, AutoCompleteTextView vp, AutoCompleteTextView ip, TextView dateView) {
        SharedPreferences priljubljene = context.getSharedPreferences("zadnjiIskaniPostaji", Context.MODE_PRIVATE);
        String fromID = priljubljene.getString("fromID", "");
        String toID = priljubljene.getString("toID", "");
        String date = priljubljene.getString("date", "");

        vp.setText(fromID, false);
        ip.setText(toID, false);
        dateView.setText(date);
    }

    /**
     * Metoda iz objektov pridobi vrednosti in jih shrani v sharedPreference
     * @param context - kontekst razreda iz katerega je klicana metoda
     * @param vp - AutoCompleteTextView za vstopno postajo
     * @param ip - AutoCompleteTextView za izstopo postajo
     * @param date - TextView za tekst koledarja
     */
    public static void shraniZadnjiIskani(Context context, AutoCompleteTextView vp, AutoCompleteTextView ip, String date) {
        SharedPreferences priljubljene = context.getSharedPreferences("zadnjiIskaniPostaji", Context.MODE_PRIVATE);
        SharedPreferences.Editor urejevalnik = priljubljene.edit();
        urejevalnik.putString("fromID", vp.getText().toString());
        urejevalnik.putString("toID", ip.getText().toString());
        urejevalnik.putString("date", date);

        urejevalnik.apply();
    }

    /**
     * Metoda vrne String imena postaje iz podanega IDja
     * @param id - id postaje
     * @return - ime postaje
     */
    public static String getIDfromMap(String id) {
        return postaje.get(id);
    }

    /**
     * Metoda vrne današnji datum
     * @return - datum v obliki teksta
     */
    public static String dodajDanasnjiDan() {
        // Današnji datum
        Calendar c = Calendar.getInstance();
        SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

        return today.format(c.getTime());
    }

    /**
     * Funkcija za pretvorbo "pixel" v "density independent pixel"
     * @param px - vrednost piklsov
     * @return - vrednost density independent pixel
     */
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Funkcija za pretvorbo "density independent pixel" v "pixel"
     * @param dp - vrednost density independent pixel
     * @return - vrednost piklsov
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Metoda nastavi obrobe tekstovnih polj glave urnika
     */
    public static Integer calcMargins(Context context) {
        Integer allMargins = 0;
        Integer displayWidth = 0;
        Integer contentWidth = DataSourcee.dpToPx(3*60+65+50);
        Integer layoutPadding = DataSourcee.dpToPx(32);

        try {
            WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics displaymatrics = new DisplayMetrics();
            display.getMetrics(displaymatrics);

            try{
                Point size = new Point();
                display.getSize(size);
                displayWidth = size.x;
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        allMargins = displayWidth - (contentWidth + layoutPadding);
        allMargins /= 10;

        return allMargins;
    }

}
