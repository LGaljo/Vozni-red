package com.lukag.atvoznired;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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

    public static String getIDfromMap(String str) {
        return postaje.get(str);
    }

}
