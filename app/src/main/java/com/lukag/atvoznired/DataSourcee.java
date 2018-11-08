package com.lukag.atvoznired;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;

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

    /*
    Metoda, ki mi iz JSON datoteke prebere vse postaje
    Vrne mi string v katerem je postaja
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

    public static void nastaviZadnjiIskani(Context context, AutoCompleteTextView vp, AutoCompleteTextView ip) {
        SharedPreferences priljubljene = context.getSharedPreferences("zadnjiIskaniPostaji", Context.MODE_PRIVATE);
        String fromID = priljubljene.getString("fromID", "");
        String toID = priljubljene.getString("toID", "");

        vp.setText(fromID, false);
        ip.setText(toID, false);
    }

    public static void shraniZadnjiIskani(Context context, AutoCompleteTextView vp, AutoCompleteTextView ip) {
        SharedPreferences priljubljene = context.getSharedPreferences("zadnjiIskaniPostaji", Context.MODE_PRIVATE);
        SharedPreferences.Editor urejevalnik = priljubljene.edit();
        urejevalnik.putString("fromID", vp.getText().toString());
        urejevalnik.putString("toID", ip.getText().toString());

        urejevalnik.apply();
    }

    public static String getIDfromMap(String str) {
        return postaje.get(str);
    }

    public static String dodajDanasnjiDan() {
        // Današnji datum
        Calendar c = Calendar.getInstance();
        SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

        return today.format(c.getTime());
    }
}
